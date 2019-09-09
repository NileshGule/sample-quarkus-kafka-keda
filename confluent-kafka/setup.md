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

helm install `
--name prometheues `
stable/prometheus

helm install `
--name grafana `
stable/grafana

```

$data = "gYXE6tHMu2QGt7rSbrIMHXEBkIDlXcf3Mn46GSSz"
[System.Text.Encoding]::Unicode.GetString([System.Convert]::FromBase64String($data))

[System.Text.Encoding]::ASCII.GetString([System.Convert]::FromBase64String(\$data))

[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String(\$data))
