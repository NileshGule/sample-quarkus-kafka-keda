# Using KEDA with Quarkus reactive Kafka messaging application

This project demonstrate using a Quarkus application using a Kafka broker to publish and consume message.  
Using KEDA to auto scale the application when the consumer is lagging behind.
 

## Environment setup

### Install GraalVM

Install GraalVM from the [Releases](https://github.com/oracle/graal/releases) page.  Use the 19.2.0+ version

Set `GRAALVM_HOME` environment variable

## Bootstrap project

Instructions for building Quarkus project using Maven can be found on this excellent [resource](https://quarkus.io/guides/maven-tooling)

```shell script

mvn io.quarkus:quarkus-maven-plugin:0.21.1:create `
    -DprojectGroupId=com.nileshgule `
    -DprojectArtifactId=quarkus-kafka-keda `
    -DclassName="com.nileshgule.quickstart.GreetingResource" `
    -Dpath="/hello" `
    -Dextensions="kafka"

```

**Note:** Sometimes the maven command doesn't execute properly. In such cases interactive mode can be used to specify each of the parameters specified above by running the command

`mvn io.quarkus:quarkus-maven-plugin:0.28.1:create`

### Add kafka extension

In case the kafka extension was not specified during the initial bootstrapping of project, it can be added later.

`./mvn quarkus:add-extension -Dextensions="kafka"`

### Add Kubernetes extension

`./mvn quarkus:add-extension -Dextensions="kubernetes"`

## Run project

### Run application using Quarkus Dev mode

`./mvn compile quarkus:dev`


### Create native image

`./mvn package -Pnative`


## Build the consumer docker image

```shell script
$ cd quarkus-kafka-keda/consumer
$ docker build -f src/main/docker/dockerfile.multistage -t nileshgule/quarkus-kafka-consumer .

# Push to dockerhub or your registry
$ docker push nileshgule/quarkus-kafka-consumer
```

## Building the producer docker image

```shell script
$ cd quarkus-kafka-keda/producer

$ docker build -f src/main/docker/dockerfile.multistage -t nileshgule/quarkus-kafka-producer .
# Push to dockerhub or your registry
$ docker push nileshgule/quarkus-kafka-producer
```

## Install Kafka cluster on Kubernetes

### Install Confluent Kafka 

```shell script

$ helm repo add confluent https://confluentinc.github.io/cp-helm-charts/

$ helm repo update

$ helm install --namespace kafka --name cp-kafka-release --set cp-schema-registry.enabled=false --set cp-kafka-rest.enabled=false --set cp-kafka-connect.enabled=false --set cp-ksql-server.enabled=false --set cp-control-center.enabled=false confluentinc/cp-helm-charts

```
### Verify that kafka is running

First check that kafka is running.

```shell script
$ kubectl -n kafka get pods

NAME                              READY   STATUS    RESTARTS   AGE
cp-kafka-release-0                2/2     Running   0          33s
cp-kafka-release-1                2/2     Running   0          30s
cp-kafka-release-2                2/2     Running   0          27s
cp-kafka-release-cp-zookeeper-0   2/2     Running   0          33s
cp-kafka-release-cp-zookeeper-1   2/2     Running   0          26s
cp-kafka-release-cp-zookeeper-2   2/2     Running   0          15s

```
### Deploy the kafka client

```shell script
cat << EOF | kubectl apply -f -
    apiVersion: v1
    kind: Pod
    metadata:
      name: kafka-client
      namespace: kafka
    spec:
      containers:
      - name: kafka-client
        image: confluentinc/cp-kafka:5.0.1
        command:
          - sh
          - -c
          - "exec tail -f /dev/null"
EOF
```

### Connect to the kafka client and create the topic 'demo'

```shell script
$ kubectl -n kafka exec -it kafka-client bash

#create kafka topic prices with 5 partitions and 3 replication-factor
root@kafka-client:/# kafka-topics --zookeeper cp-kafka-release-cp-zookeeper-headless:2181 --topic prices --create --partitions 5 --replication-factor 3 --if-not-exists
Created topic "prices".

```

### Deploy the Producer application

`kubectl apply -f k8s/producer-deployment.yml`

### Verify that there are messages in the topic

From the kafka-client pod, execute the command below.

```shell script
#check if the number of messages and partitions in the topic

root@kafka-client:/# kafka-run-class kafka.tools.GetOffsetShell --broker-list cp-kafka-release-headless:9092 --topic prices
prices:0:5
prices:1:5
prices:2:5
prices:3:5
prices:4:5
``` 

In the example above, it means you have 25 messages in the topic `prices`, with 5 messages in each partition.

### Deploy keda

Instructions on how to deploy keda [here](https://keda.sh/deploy/)

### Deploy the consumer application

`kubectl apply -f k8s/consumer-deployment.yml`

### Check if consumer is consuming messages

From the kafka-client pod, execute the following command.

```shell script
root@kafka-client:/# kafka-consumer-groups --bootstrap-server cp-kafka-release-headless:9092 --group demo --describe

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
prices          3          152             212             60              consumer-1-a1d7e1c8-55f7-4400-aee2-f74acd0dee12 /10.1.11.19     consumer-1
prices          2          191             212             21              consumer-1-a1d7e1c8-55f7-4400-aee2-f74acd0dee12 /10.1.11.19     consumer-1
prices          0          190             211             21              consumer-1-a1d7e1c8-55f7-4400-aee2-f74acd0dee12 /10.1.11.19     consumer-1
prices          4          190             212             22              consumer-1-a1d7e1c8-55f7-4400-aee2-f74acd0dee12 /10.1.11.19     consumer-1
prices          1          151             212             61              consumer-1-a1d7e1c8-55f7-4400-aee2-f74acd0dee12 /10.1.11.19     consumer-1
```

### Deploy the `ScaledObject`

`kubectl apply -f k8s/scaledobject.yaml`

Below is the scaled object definition

```yaml
apiVersion: keda.k8s.io/v1alpha1
kind: ScaledObject
metadata:
  name: quarkus-kafka-keda-scaled-object
  namespace: kafka
  labels:
    deploymentName: quarkus-kafka-consumer

spec:
  pollingInterval: 10   # Optional. Default: 30 seconds
  cooldownPeriod: 30   # Optional. Default: 300 seconds
  minReplicaCount: 0   # Optional. Default: 0
  maxReplicaCount: 5  # Optional. Default: 100
  scaleTargetRef:
    deploymentName: quarkus-kafka-consumer
  triggers:
    - type: kafka
      metadata:
        type: kafkaTrigger
        direction: in
        name: event
        topic: prices
        brokerList: cp-kafka-release-headless.kafka:9092 # from the newer keda master build
        brokers: cp-kafka-release-headless.kafka:9092
        consumerGroup: demo
        dataType: binary
        lagThreshold: "60"
```

Important bits:

 * `lagThreshold` - This is the threshold to determine whether to scale up or down the pods.
 * `brokerList` - The `host:port` of the kafka broker.
 * `topic` - The `topic` where the Kafka scaler will listen to.
 * `consumerGroup` - This is the consumer group belonging to the consumer pods.

You should start to see the consumer pods scaling up.  Something like this

```shell script
$ kubectl -n kafka get pods

NAME                                      READY   STATUS    RESTARTS   AGE
quarkus-kafka-consumer-59c498c854-cwthd   1/1     Running   0          4m1s
quarkus-kafka-consumer-59c498c854-hl7t2   1/1     Running   0          48s
quarkus-kafka-consumer-59c498c854-lp58b   1/1     Running   0          48s
quarkus-kafka-consumer-59c498c854-rhth7   1/1     Running   0          48s
quarkus-kafka-consumer-59c498c854-z42gx   1/1     Running   0          32s
quarkus-kafka-producer-bb5bc6b6d-c5qhk    1/1     Running   0          6m27s
```

**Also take note that the maximum pods that you can scale up to is the number of partitions defined in the topic.**

To see how the pods are scaled down, you need to wait for the Kubernetes HPA to kick in, which is normally set to 5 mins.

```shell script
$ kubectl -n kafka get pods
NAME                                      READY   STATUS        RESTARTS   AGE
quarkus-kafka-consumer-59c498c854-cwthd   1/1     Running       0          10m
quarkus-kafka-consumer-59c498c854-lp58b   1/1     Running       0          7m29s
quarkus-kafka-consumer-59c498c854-rhth7   0/1     Terminating   0          7m29s
quarkus-kafka-producer-bb5bc6b6d-c5qhk    1/1     Running       0          13m

#Check the hpa
$ kubectl -n kafka get hpa

NAME                              REFERENCE                           TARGETS           MINPODS   MAXPODS   REPLICAS   AGE
keda-hpa-quarkus-kafka-consumer   Deployment/quarkus-kafka-consumer   18500m/60 (avg)   1         5         2          8m21s
```

##  Scale to zero

To scale to zero, simply scale the producer pod to zero.

Since in the [`ScaledObject`](k8s/scaledobject.yaml) file, the `cooldownPeriod` is set to 30s.

This mean once there are no more messages coming in, Keda will wait for about 30s before it scales it to zero.

```shell script
$ kubectl -n kafka scale deployment quarkus-kafka-produce --replicas=0

$ kubectl -n kafka get pods
NAME                                      READY   STATUS        RESTARTS   AGE
quarkus-kafka-consumer-59c498c854-cwlcx   0/1     Terminating   0          50s
quarkus-kafka-consumer-59c498c854-h872j   0/1     Terminating   0          50s
quarkus-kafka-consumer-59c498c854-m7nlz   0/1     Terminating   0          50s
quarkus-kafka-consumer-59c498c854-sf6cb   0/1     Terminating   0          63s
quarkus-kafka-consumer-59c498c854-zgz24   0/1     Terminating   0          34s
```

Check in kafka-client that there are no more consumers listening.

```shell script
root@kafka-client:/# kafka-consumer-groups --bootstrap-server cp-kafka-release-headless:9092 --group demo --describe
Consumer group 'demo' has no active members.

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
prices          3          1212            1212            0               -               -               -
prices          2          1211            1211            0               -               -               -
prices          0          1212            1212            0               -               -               -
prices          4          1212            1212            0               -               -               -
prices          1          1211            1211            0               -               -               -
```


#### Useful Kafka commands

```shell script
# Put message into the topic
echo "{\"text\": \"this is not something nice to say\"}" | kafka-console-producer --broker-list cp-kafka-release-headless:9092 --topic prices

#get consumer group offset
kafka-consumer-groups --bootstrap-server cp-kafka-release-headless:9092 --group demo --describe

#reset the offset, only works if there are no consumers
kafka-consumer-groups --bootstrap-server cp-kafka-release-headless:9092 --group demo --reset-offsets --to-latest --topic prices --execute

kafka-console-producer --broker-list cp-kafka-release-headless:9092 --topic prices

kafka-console-producer --broker-list cp-kafka-release-headless:9092 --topic prices

kafka-console-consumer --bootstrap-server cp-kafka-release-headless:9092 --topic prices --from-beginning
```
