$limit = (Get-Date).AddMinutes(-30)
$path = "E:\LASAutoArchiving\LandingZone"

Get-ChildItem -Path $path -Force | Where-Object {$_.CreationTime -lt $limit -and !$_.PSIsContainer} | Select-Object Name
