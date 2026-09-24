param(
  [Parameter(Mandatory=$true)][string]$DockerHubUser
)
$ErrorActionPreference = "Stop"
$tmp = Join-Path $env:TEMP "taskboard-k8s"
if (Test-Path $tmp) { Remove-Item -Recurse -Force $tmp }
Copy-Item -Recurse "$PSScriptRoot/../kubernetes-config" $tmp
Get-ChildItem $tmp -Filter *.yaml | ForEach-Object {
  (Get-Content $_.FullName) -replace 'DOCKERHUB_USER', $DockerHubUser | Set-Content $_.FullName
}
kubectl apply -f "$tmp/10-postgres-secret.yaml"
kubectl apply -f "$tmp/12-postgres.yaml"
kubectl apply -f "$tmp/20-user.yaml"
kubectl apply -f "$tmp/30-task.yaml"
kubectl apply -f "$tmp/40-frontend.yaml"
Write-Host "Waiting for pods..."
kubectl wait --for=condition=ready pod -l app=postgres --timeout=180s
kubectl get pods,svc,pvc
Write-Host ""
Write-Host "Open http://localhost in your browser."
Write-Host "Logs: kubectl logs -l app=task-service --tail=50"
