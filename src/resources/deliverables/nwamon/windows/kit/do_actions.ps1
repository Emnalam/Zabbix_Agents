$this_target="Bendura Production - Equalizer"
$log_file="C:\nwamon\log\actions.log"
$file="C:\nwamon\actions\command.act"
$previous_checksum_file = "C:\nwamon\actions\command.checksum"

if (Test-Path $file)
{

	[XML]$actionDetails = Get-Content $file
	$target = $actionDetails.action.target
	Write-Output $target
	if ($target -eq $this_target)
	{
		$md5 = New-Object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider

		$hash = [System.BitConverter]::Tostring($md5.ComputeHash([System.IO.File]::ReadAllbytes($file)))
	
		if (Test-Path $previous_checksum_file)
		{
			$previous_sum=[System.IO.File]::ReadAllText($previous_checksum_file)

			if ($previous_sum -eq $hash)
			{
				Write-Output "Same hash not executing..."
				return
			}
		}
	
		[System.IO.File]::WriteAllText($previous_checksum_file, $hash)
		
		Write-Output "Executing Service Start..."
		Start-Service tomcat8
		Write-Output "Done"
		$log_entry = "$(Get-Date) Done executing action"
		
		[System.IO.File]::AppendAllText($log_file, $log_entry)
		[System.IO.File]::AppendAllText($log_file, "`r`n")
	}
	else
	{
		Write-Output "Not for this target"
	}

}

