# sample-quarkus-kafka-keda

Example of using Quarkus with Kafka consumer with KEDA

## Environment setup

### Install GraalVM

Install GraalVM from the [Releases](https://github.com/oracle/graal/releases) page

Set `GRAALVM_HOME` environment variable

## Bootstrap project

Instructions for building Quarkus project using Maven can be found on this excellent [resource](https://quarkus.io/guides/maven-tooling)

```code

mvn io.quarkus:quarkus-maven-plugin:0.21.1:create `
    -DprojectGroupId=com.nileshgule `
    -DprojectArtifactId=quarkus-kafka-keda `
    -DclassName="com.nileshgule.quickstart.GreetingResource" `
    -Dpath="/hello" `
    -Dextensions="kafka"

```

**Note:** Sometimes the maven command doesn't execute properly. In such cases interactive mode can be used to specify each of the parameters specified above by running the command

```code

mvn io.quarkus:quarkus-maven-plugin:0.21.1:create

```

### Add kafka extension

In case the kafka extension was not specified during the initial bootstrapping of project, it can be added later.

```code

./mvnw quarkus:add-extension -Dextensions="kafka"

```

## Run project

### Run application using Quarkus Dev mode

```code

.\mvnw compile quarkus:dev

```

### Create native image

```code

.\mnvw package -Pnative

```

### Build native image using docker

```code

docker build -f src/main/docker/dockerfile.multistage -t nileshgule/quarkus-kafka-keda .

```

### Push image to DockerHub

```

docker push nileshgule/quarkus-kafka-keda

```

### Add Kubernetes extension

```code

./mvnw quarkus:add-extension -Dextensions="kubernetes"

```

## Install Kafka cluster on Kubernetes

### Install Confluent Kafka 

```shell

helm repo add confluent https://confluentinc.github.io/cp-helm-charts/

helm repo update

helm install --namespace kafka --name cp-kafka-release --set cp-schema-registry.enabled=false --set cp-kafka-rest.enabled=false --set cp-kafka-connect.enabled=false --set cp-ksql-server.enabled=false --set cp-control-center.enabled=false confluentinc/cp-helm-charts

```

### Useful Kafka commands

```shell

#create kafka topic prices with 5 partitions and 3 replication-factor
kafka-topics --zookeeper cp-kafka-release-cp-zookeeper-headless:2181 --topic prices --create --partitions 5 --replication-factor 3 --if-not-exists

#check if the number of messages and partitions in the topic
kafka-run-class kafka.tools.GetOffsetShell --broker-list cp-kafka-release-headless:9092 --topic prices

#put message into the topic
echo "{\"text\": \"this is not something nice to say\"}" | kafka-console-producer --broker-list cp-kafka-release-headless:9092 --topic prices

#get consumer group offset
kafka-consumer-groups --bootstrap-server cp-kafka-release-headless:9092 --group demo --describe

#sample output
TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
prices          2          1489            2401            912             consumer-1-b25a1fde-7fd1-4fc6-b26c-e007a36d7e0e /10.1.11.44     consumer-1
prices          3          1501            2403            902             consumer-1-cd1368e7-8fbe-4f7b-b684-61c4b067be71 /10.1.11.42     consumer-1
prices          4          1502            2402            900             consumer-1-d7732da5-469c-44ac-8539-b4ef7ac24b32 /10.1.11.43     consumer-1
prices          1          1488            2403            915             consumer-1-9af9f3c0-1392-4a26-ad85-37e2bc30ce42 /10.1.11.46     consumer-1
prices          0          1492            2401            909             consumer-1-6b8bce74-7853-4433-9cf6-966cd49709ab /10.1.11.45     consumer-1



kafka-console-producer --broker-list cp-kafka-release-headless:9092 --topic prices

kafka-console-producer --broker-list cp-kafka-release-headless:9092 --topic prices

kafka-console-consumer --bootstrap-server cp-kafka-release-headless:9092 --topic prices --from-beginning


```

### Port forward Kafka-manager service

```shell

kubectl port-forward svc/kafka-manager 8085:80

kubectl port-forward svc/waxen-robin-prometheus-server 8081:80

kubectl port-forward svc/grafana 8091:80

```
