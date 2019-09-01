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

In case the kafka extension was not specified during the iniital bootstrapping of project, it can be added later.

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
