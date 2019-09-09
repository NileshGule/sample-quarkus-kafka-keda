# Setup Confluent Kafka using Helm chart

## Add Helm repo

```code

helm repo add confluent https://confluentinc.github.io/cp-helm-charts/

helm repo update

helm install `
--name cp-kafka-release `
--set cp-schema-registry.enabled=false `
--set cp-kafka-rest.enabled=false `
--set cp-kafka-connect.enabled=false `
confluent/cp-helm-charts

helm install stable/prometheus

helm install stable/grafana

```
