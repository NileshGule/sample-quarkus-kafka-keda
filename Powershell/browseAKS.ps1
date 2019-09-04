Param(
    [parameter(Mandatory = $false)]
    [string]$resourceGroupName = "quarkuskedaRG",
    [parameter(Mandatory = $false)]
    [string]$clusterName = "quarkuskedaCluster"
)

# Browse AKS dashboard
Write-Host "Browse AKS cluster $clusterName" -ForegroundColor Yellow
az aks browse `
    --resource-group=$resourceGroupName `
    --name=$clusterName