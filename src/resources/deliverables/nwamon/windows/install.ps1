
 param (
  [switch]$UPDATE  = $false,
  [string]$DIR
)

Push-Location $PSScriptRoot

$global:Dir=""
$global:serviceExists="0"
$serviceName="Nwamon"


function SelectFolder() {
	[System.Reflection.Assembly]::LoadWithPartialName("System.windows.forms")|Out-Null

  $app = new-object -com Shell.Application
  $folder = $app.BrowseForFolder(0, "Select Folder", 0, "")
  if ($folder.Self.Path -ne "") {
    $global:Dir = $folder.Self.Path
    Log "Installation folder is: $global:Dir" 
  }
}

function StopServiceIfExists() {
		If (Get-Service $serviceName) {
			Log "New Access Monitoring agents Service exists"
			$global:serviceExists="1"
			If ((Get-Service $serviceName).Status -eq 'Running') {
				Log "Stopping Service"
				Stop-Service $serviceName
			}
			else {
				Log "Service is not running"
			}
		} 
		else
		{
			$error.clear()
		}
}

function StartServiceIfExists() {
  If (Get-Service $serviceName) {
    Log "New Access Monitoring agents Service exists"
    $global:serviceExists="1"
    If ((Get-Service $serviceName).Status -eq 'Running') {
      Log "Service is already running"
    }
    else {
      Log "Starting service..."
      Start-Service $serviceName
    }
  } 
  else
  {
    $error.clear()
  }
}

function CreateServiceIfNotExists() {
	if ($serviceExists -eq "0") {
		Log "New Access Monitoring agents Service does not exist, creating service..."
		Log "Please set the credentials in the popup..."
		$command = "$global:Dir\bin\NwamonService.exe -install"
		iex $command		
		If (Get-Service $serviceName) {
			Log "Service created successfully, please wait..."
		}
		else {
			Log "Failed to create service, please check the log file:  $PSScriptRoot\InstallUtil.InstallLog"
		}
	}
}

function CopyBin() {
	Log "Copying new bin components..."
	if (Test-Path "$global:Dir\bin") {
    $exclude = @('transfer.bat','check_updates.bat')
	  Copy-Item kit\* -Destination $global:Dir\bin -Force -Recurse -Container -Exclude $exclude
    Copy-Item ../agents.jar $global:Dir/bin/
    if (-not(Test-Path "$global:Dir\bin\transfer.bat"))
    {
      Copy-Item kit\transfer.bat -Destination $global:Dir\bin -Force
    }
    if (-not (Test-Path "$global:Dir\bin\check_updates.bat"))
    {
      Copy-Item kit\check_updates.bat -Destination $global:Dir\bin -Force
    }
	}
	else 
	{
	  Copy-Item kit -Destination $global:Dir\bin -Recurse
	  Copy-Item ../agents.jar $global:Dir/bin/
	}
}

function Log {
  param (
    [string]$MSG = ""
  )
  if ($outputBox)
  {
    $outputBox.Text = $outputBox.Text + "`n" + $MSG
  }
  else{
    Write-Host $MSG
  }

}

function CopyRun() {
	if (-not (Test-Path "$global:Dir\dat")) {
	  Log "Creating dat, log and output folders..."
	  New-Item -ItemType Directory -Path $global:Dir -Name "dat"
	  New-Item -ItemType Directory -Path $global:Dir -Name "log"
	  New-Item -ItemType Directory -Path $global:Dir -Name "output"
  }
  if (-not (Test-Path "$global:Dir\updates")) {
    Log "Creating updates folder..."
    New-Item -ItemType Directory -Path $global:Dir -Name "updates"
  }
}

function CheckAutomaticUpdates()
{
  if ($global:Dir -eq "")
  {
    log "Please select a valid installation folder first. If it is the first install, then install the package before checking automatic updates..."
  }
  else 
  {
    Log "Checking basic configuration for automatic updates..."
    Update( $global:Dir )
    if ($error.Count -eq 0)
    {
      Log ""
      Log "Basic configuration seems to be ok."
      Log "Please complete the check with the below information:"
      Log "- Check that the user running nwamon service has the rights to read-write on the installation folder"
      Log "- Use the Tools\ServiceSecurityEditor.exe to check that the user running nwamon service has the rights to read/stop/start nwamon service."
    }
  }
}

function Install() {
	$global:serviceExists="0"
	ClearTextBox
	Log "Installing..."
	StopServiceIfExists
	if ($error.Count -eq 0)
	{
    log "Please wait..."
    Start-Sleep -s 2
		CopyBin
		if ($error.Count -eq 0)
		{
			CreateServiceIfNotExists
			if ($error.Count -eq 0)
			{
				CopyRun
				if ($error.Count -eq 0)
				{
					ShowSuccess
					log "Setup completed successfully"
				}
				else 
				{
					ShowFailed
					log $error[0]
				}
			}
			else 
			{
				ShowFailed
				log $error[0]
			}
		}
		else 
		{
			ShowFailed
			log $error[0]
		}
	}
	else
	{
		ShowFailed
		log $error[0]
	}
	
}

function ClearTextBox()
{
  if ($outputBox)
  {
    $outputBox.Text = ""
  }
}

function ShowSuccess()
{
  if ($agStatValue)
  {
    $agStatValue.Text = "Success"
    $agStatValue.ForeColor = 'green'
  }
}

function ShowFailed()
{
  if ($agStatValue)
  {
    $agStatValue.Text = "Failed"
    $agStatValue.ForeColor = 'red'
  }
}

function Update ([String] $installDir) {
  if([System.IO.Directory]::Exists($installDir)){
    $jarFile = Join-Path $installDir "bin/agents.jar"
    if ([System.IO.File]::Exists($jarFile))
    {
      $global:Dir  = $installDir
      Install
      if ($error.Count -eq 0)
      {
        StartServiceIfExists 
        if ($error.Count -eq 0)
        {
          log "Nwamon was successfully updated and service started"
        }
        else
        {
          log "Update failed"
        }
      }
      else 
      {
        log "Update failed"
      }
    }
    else
    {
      log "Provided path does not seem to be nwamon's path:  $installDir"
    }
  }
  else
  {
    log "Provided path does not exist:  $installDir"
  }
}

function Gui {
    $script:GUI = $true

    #### Form settings #################################################################
    [void] [System.Reflection.Assembly]::LoadWithPartialName("System.Drawing") 
    [void] [System.Reflection.Assembly]::LoadWithPartialName("System.Windows.Forms")  
 
    $Form = New-Object System.Windows.Forms.Form
    $Form.FormBorderStyle = [System.Windows.Forms.FormBorderStyle]::FixedSingle
    $Form.Text = "NWAMON Windows Installation Program"   
    $Form.Size = New-Object System.Drawing.Size(690,430)  
    $Form.StartPosition = "CenterScreen" #loads the window in the center of the screen
    $Form.BackgroundImageLayout = "Zoom"
    $Form.MinimizeBox = $False
    $Form.MaximizeBox = $True
    $Form.WindowState = "Normal"
    $Form.SizeGripStyle = "Hide"
    $Icon = [system.drawing.icon]::ExtractAssociatedIcon($PSHOME + "\powershell.exe")
    $Form.Icon = $Icon
 
    #### Title - Powershell GUI Tool ###################################################
    $Label = New-Object System.Windows.Forms.Label
    $LabelFont = New-Object System.Drawing.Font("Calibri",18,[System.Drawing.FontStyle]::Bold)
    $Label.Font = $LabelFont
    $Label.Text = "NewAccess Monitoring Agents Installation"
    $Label.AutoSize = $True
    $Label.Location = New-Object System.Drawing.Size(120,20) 
    $Form.Controls.Add($Label)
 
###################### BUTTONS ##########################################################
 
    #### Group boxes for buttons ########################################################
    $groupBox = New-Object System.Windows.Forms.GroupBox
    $groupBox.Location = New-Object System.Drawing.Size(45,60) 
    $groupBox.size = New-Object System.Drawing.Size(600,90)
    $groupBox.text = ""
    $Form.Controls.Add($groupBox) 
 
    #### Select Button #################################################################
    $agRun = New-Object System.Windows.Forms.Button
    $agRun.Location = New-Object System.Drawing.Size(10,13)
    $agRun.Size = New-Object System.Drawing.Size(200,30)
    $agRun.Text = "Select installation folder"
    $agRun.Add_Click({SelectFolder})
    $agRun.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agRun)
 
     #### Install Button #################################################################
    $agInst = New-Object System.Windows.Forms.Button
    $agInst.Location = New-Object System.Drawing.Size(275,13)
    $agInst.Size = New-Object System.Drawing.Size(100,30)
    $agInst.Text = "Install"
    $agInst.Add_Click({Install})
    $agInst.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agInst)

	#### Status Label #################################################################
    $agStat = New-Object System.Windows.Forms.Label
    $agStat.Location = New-Object System.Drawing.Size(435,13)
    $agStat.Size = New-Object System.Drawing.Size(50,30)
    $agStat.Text = "Status: "
    $agStat.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agStat)
	
	
	#### Status #################################################################
    $agStatValue = New-Object System.Windows.Forms.Label
    $agStatValue.Location = New-Object System.Drawing.Size(500,13)
    $agStatValue.Size = New-Object System.Drawing.Size(70,30)
    $agStatValue.Text = "Not started"
    $agStatValue.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agStatValue)

  #### Check automatic updates Button #################################################################
  $agUpd = New-Object System.Windows.Forms.LinkLabel
  $agUpd.Location = New-Object System.Drawing.Size(230,65)
  $agUpd.Size = New-Object System.Drawing.Size(250,15)
  $agUpd.Text = "Check Automatic Updates"
  $agUpd.Add_Click({CheckAutomaticUpdates})
  $agUpd.Cursor = [System.Windows.Forms.Cursors]::Hand
  $groupBox.Controls.Add($agUpd)


 
  
###################### END BUTTONS ######################################################
 
    #### Output Box Field ###############################################################
    $outputBox = New-Object System.Windows.Forms.RichTextBox
    $outputBox.Location = New-Object System.Drawing.Size(25,155) 
    $outputBox.Size = New-Object System.Drawing.Size(635,230)
    $outputBox.Font = New-Object System.Drawing.Font("Consolas", 8 ,[System.Drawing.FontStyle]::Regular)
    $outputBox.MultiLine = $True
    $outputBox.ScrollBars = "Vertical"
    $outputBox.Text = ""
    $Form.Controls.Add($outputBox)
 
    ##############################################
 
    $Form.Add_Shown({$Form.Activate()})
    [void] $Form.ShowDialog()
}

if ($UPDATE) {
  Update $DIR
}
else 
{
  Gui
}



