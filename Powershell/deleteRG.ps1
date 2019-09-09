Param(
    [parameter(Mandatory = $false)]
    [string]$resourceGroupName = "quarkuskedaRG"
)

# Delete AKS cluster
Write-Host "Deleting resource group $resourceGroupName" -ForegroundColor Red
az group delete --name=$resourceGroupName --yes