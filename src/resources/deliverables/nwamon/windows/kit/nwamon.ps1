<#
 # New Access Monitoring Agents - Windows Agents control script
 #
 # History:
 # 2019.04.08 - FRD - Creation  
 # 2019.04.09 - FRD - GUI function
 # 2019.04.17 - FRD - Change current dir to bin\..
 # 2019.04.23 - FRD - runAs Admin and PID file
 # 2019.07.02 - RSA - Show agents version
 #>

param (
  [switch]$HELP     = $false,
  [switch]$RUN      = $false,
  [switch]$KILL     = $false,
  [switch]$STATUS   = $false,
  [switch]$CHECK    = $false,
  [switch]$VERSION  = $false,
  [string]$HOMEDIR  = "",
  [string]$XML      = ""
)

# Print-out on CLI or on GUI
[switch]$GUI = $false
[string]$PIDFILE="dat\nwamon.pid"

function usage {
  "

    New Access monitoring agents
   
    usage: nwamon.ps1 [-h] [-r|-k|-c] [-s] [-x settings.xml]
    
             -help        Usage (this text)
             -run         Launch the agents
             -kill        Stop the running agents
             -check       Checks an XML parameter file (Agents have to be stopped for this option)
             -version     Shows implementation version and Java sdk buil version
             -status      Control the status of the Agents. Gives back the corresponding PID if started
             -xml <file>  Agents parameters XML file
                          default : dat/agentsSettings.xml

  "
}

function log {
  param (
    [string]$MSG = ""
  )
 
  if ($GUI) {
    $outputBox.Text = $outputBox.Text + "`n" + $MSG
  } else {
    Write-Host $MSG
  }
}

function pid {
  $P=Get-WmiObject win32_process -Filter "CommandLine like '%Starter%'"|Select-Object ProcessId
  $P.ProcessId
}

function agentsStart {
  $P = pid
  log ""
  if (! $P) {
    log "Starting agents...."
    javaw -classpath "$env:CLASSPATH%;.;bin/agents.jar" Starter $SETTINGS
    $P = pid
	$P > $PIDFILE
    log "Agents started with PID $P"
  } else {
    log "Agents are already running with PID $P"
  }
}

function agentsStop {
  $P = Get-Content $PIDFILE
  log ""
  if ($P) {
    log "Stopping agents with PID $P"
    Stop-Process -id $P -force
    $Ps = Get-WmiObject win32_process -Filter "parentprocessid = $P"|Select-Object ProcessId
    foreach ($S in $Ps) {
      $Sid = $S.ProcessId
      Stop-Process -id $Sid -force
      log "Sub-Process $Sid stopped"
    }
	Remove-Item -path $PIDFILE -force
    log "Stopped"
  } else {
    log "No agent running..."
  }
}

function agentsStatus {
  $P = pid
  log ""
  if (! $P) {
    log "No agent running..."
  }
  else {
    $Ps = Get-WmiObject win32_process -Filter "parentprocessid = $P"|Select-Object ProcessId
    log "Agents running with PID $P"
    foreach ($S in $Ps) {
      $Sid = $S.ProcessId
      log "- Sub-Processes running with PID $Sid"
    }
  }
}

function agentsCheck {
  $P = pid
  if (! $P) {
    log "Checking config...."
    java -classpath "$env:CLASSPATH%;.;bin/agents.jar" Starter $SETTINGS checkConfigOnly
    log "Done"
  } else {
    log "Agents running with PID $P - Please stop them before calling this option"
    usage
  }
}

function agentsVersion {
  log ""
  log "Agents Implementation and Jdk build versions:"
  $infos = & java -classpath "$env:CLASSPATH%;.;bin/agents.jar" Starter version
  $infos1, $infos2 = $infos.split([Environment]::NewLine)
  log "$infos1"
  log "$infos2"
}

function gui {
    $script:GUI = $true

    #### Form settings #################################################################
    [void] [System.Reflection.Assembly]::LoadWithPartialName("System.Drawing") 
    [void] [System.Reflection.Assembly]::LoadWithPartialName("System.Windows.Forms")  
 
    $Form = New-Object System.Windows.Forms.Form
    $Form.FormBorderStyle = [System.Windows.Forms.FormBorderStyle]::FixedSingle
    $Form.Text = "NWA-Mon"   
    $Form.Size = New-Object System.Drawing.Size(685,400)  
    $Form.StartPosition = "CenterScreen" #loads the window in the center of the screen
    $Form.BackgroundImageLayout = "Zoom"
    $Form.MinimizeBox = $False
    $Form.MaximizeBox = $False
    $Form.WindowState = "Normal"
    $Form.SizeGripStyle = "Hide"
    $Icon = [system.drawing.icon]::ExtractAssociatedIcon($PSHOME + "\powershell.exe")
    $Form.Icon = $Icon
 
    #### Title - Powershell GUI Tool ###################################################
    $Label = New-Object System.Windows.Forms.Label
    $LabelFont = New-Object System.Drawing.Font("Calibri",18,[System.Drawing.FontStyle]::Bold)
    $Label.Font = $LabelFont
    $Label.Text = "NewAccess Monitoring Agents"
    $Label.AutoSize = $True
    $Label.Location = New-Object System.Drawing.Size(170,20) 
    $Form.Controls.Add($Label)
 
###################### BUTTONS ##########################################################
 
    #### Group boxes for buttons ########################################################
    $groupBox = New-Object System.Windows.Forms.GroupBox
    $groupBox.Location = New-Object System.Drawing.Size(25,60) 
    $groupBox.size = New-Object System.Drawing.Size(635,50)
    $groupBox.text = ""
    $Form.Controls.Add($groupBox) 
 
    #### RUN Button #################################################################
    $agRun = New-Object System.Windows.Forms.Button
    $agRun.Location = New-Object System.Drawing.Size(10,13)
    $agRun.Size = New-Object System.Drawing.Size(100,30)
    $agRun.Text = "Start Agents"
    $agRun.Add_Click({agentsStart})
    $agRun.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agRun)
 
    #### Kill Button #################################################################
    $agKill = New-Object System.Windows.Forms.Button
    $agKill.Location = New-Object System.Drawing.Size(175,13)
    $agKill.Size = New-Object System.Drawing.Size(100,30)
    $agKill.Text = "Stop Agents"
    $agKill.Add_Click({agentsStop})
    $agKill.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agKill)
 
    #### Status Button #################################################################
    $agStatus = New-Object System.Windows.Forms.Button
    $agStatus.Location = New-Object System.Drawing.Size(335,13)
    $agStatus.Size = New-Object System.Drawing.Size(100,30)
    $agStatus.Text = "Status of Agents"
    $agStatus.Add_Click({agentsStatus})
    $agStatus.Cursor = [System.Windows.Forms.Cursors]::Hand
    $groupBox.Controls.Add($agStatus)

     #### Version Button #################################################################
     $agVersion = New-Object System.Windows.Forms.Button
     $agVersion.Location = New-Object System.Drawing.Size(500,13)
     $agVersion.Size = New-Object System.Drawing.Size(120,30)
     $agVersion.Text = "Version of Agents"
     $agVersion.Add_Click({agentsVersion})
     $agVersion.Cursor = [System.Windows.Forms.Cursors]::Hand
     $groupBox.Controls.Add($agVersion)
  
###################### END BUTTONS ######################################################
 
    #### Output Box Field ###############################################################
    $outputBox = New-Object System.Windows.Forms.RichTextBox
    $outputBox.Location = New-Object System.Drawing.Size(25,120) 
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

if (! $HOMEDIR) {
  # cd to script parent directory
  $HOMEDIR = split-path -parent $MyInvocation.MyCommand.Definition
  $HOMEDIR = "$HOMEDIR\.."
}
cd $HOMEDIR

$SETTINGS = "dat\agentsSettings.xml"
if ($XML) {
  $SETTINGS = $XML
}

if (-not (Test-Path "$SETTINGS")) {
  usage
  "Settings file ($SETTINGS) not found"
  break
}

if       ($RUN) {
  agentsStart
} elseif ($HELP) {
  usage
} elseif ($KILL) {
  agentsStop
} elseif ($STATUS) {
  agentsStatus
} elseif ($CHECK) {
  agentsCheck
} elseif ($VERSION) {
  agentsVersion
} else {
  gui
}

# SIG # Begin signature block
# MIINDAYJKoZIhvcNAQcCoIIM/TCCDPkCAQExCzAJBgUrDgMCGgUAMGkGCisGAQQB
# gjcCAQSgWzBZMDQGCisGAQQBgjcCAR4wJgIDAQAABBAfzDtgWUsITrck0sYpfvNR
# AgEAAgEAAgEAAgEAAgEAMCEwCQYFKw4DAhoFAAQUMXVQwOdRuT1nbYxU9Kvs7n4Y
# i5igggpOMIIFFjCCA/6gAwIBAgIQCHb/m1ERN2T2L/Cxqzq8TDANBgkqhkiG9w0B
# AQsFADByMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYD
# VQQLExB3d3cuZGlnaWNlcnQuY29tMTEwLwYDVQQDEyhEaWdpQ2VydCBTSEEyIEFz
# c3VyZWQgSUQgQ29kZSBTaWduaW5nIENBMB4XDTE5MDUyODAwMDAwMFoXDTIxMDUx
# MjEyMDAwMFowUzELMAkGA1UEBhMCQ0gxFDASBgNVBAcTC0xlcyBBY2FjaWFzMRYw
# FAYDVQQKEw1OZXcgQWNjZXNzIFNBMRYwFAYDVQQDEw1OZXcgQWNjZXNzIFNBMIIB
# IjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo2nkHD+eY2m2H8i1PUOQouYN
# EI1qxNkQZlWWFsELbMlDeh4VAJ/zz5YPb5qnkw1uJ0BS+5WGnOKtvZggvYN2s3wE
# xULrR/iWSXNHiDM5nErrGIZnkET+gDZVpD5lv5EZJNm7Ws4nEtKOfvG4OWGfdwAW
# m+XGQ4XRHarD9HUloaCzzESJCjzduLr0Nr6BRb4LHntd1CUy01X/NOEJHjtsAw5I
# 0IjszMGVJApsjCEyUpId60f7w9/+zaun26RTuMCOJMiL8nYa6HmDUS+Y+dLTeUEQ
# vo5fd9WLOvxWj6a3wJOC0ZKAIJotXq89PgJMjFqgrPzmCof/A3a1OoIsvJ6O9wID
# AQABo4IBxTCCAcEwHwYDVR0jBBgwFoAUWsS5eyoKo6XqcQPAYPkt9mV1DlgwHQYD
# VR0OBBYEFNNc3u3DIamhi/9sxZc3dGnFw9xLMA4GA1UdDwEB/wQEAwIHgDATBgNV
# HSUEDDAKBggrBgEFBQcDAzB3BgNVHR8EcDBuMDWgM6Axhi9odHRwOi8vY3JsMy5k
# aWdpY2VydC5jb20vc2hhMi1hc3N1cmVkLWNzLWcxLmNybDA1oDOgMYYvaHR0cDov
# L2NybDQuZGlnaWNlcnQuY29tL3NoYTItYXNzdXJlZC1jcy1nMS5jcmwwTAYDVR0g
# BEUwQzA3BglghkgBhv1sAwEwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGln
# aWNlcnQuY29tL0NQUzAIBgZngQwBBAEwgYQGCCsGAQUFBwEBBHgwdjAkBggrBgEF
# BQcwAYYYaHR0cDovL29jc3AuZGlnaWNlcnQuY29tME4GCCsGAQUFBzAChkJodHRw
# Oi8vY2FjZXJ0cy5kaWdpY2VydC5jb20vRGlnaUNlcnRTSEEyQXNzdXJlZElEQ29k
# ZVNpZ25pbmdDQS5jcnQwDAYDVR0TAQH/BAIwADANBgkqhkiG9w0BAQsFAAOCAQEA
# hF6uZFrpllX4wtUPFkKGQagAwe0rHdFAX+z42dcFGGqijsfho9ucIH/zQOnt3RyA
# +NJTEaVEwJJ0WBhLKHowNrS6oWmBhj5boJ+Jdgin9lcdzZxMKtqSGNCQmvqIe8fA
# ZVKj9AsNKxq61cKfNVEKcI7nM6r+sU2ZdJt1J8EJ7pZhsZ+J2aNb9bpvzAg2ozOQ
# 7wwKHt8phTrJu2JuStRmIuKCtdyGWxK0hh0cvSM47l/ASXhcP9ArDMP66MHh6Jk9
# WbxIKgKRCJKKuCPSt0pZcEcPdwitDhy8hsfoBLL51FxuTRh+jDLLAlwqYKuYYNsw
# 0cKvdrDjREgGnCXptv90qDCCBTAwggQYoAMCAQICEAQJGBtf1btmdVNDtW+VUAgw
# DQYJKoZIhvcNAQELBQAwZTELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0
# IEluYzEZMBcGA1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTEkMCIGA1UEAxMbRGlnaUNl
# cnQgQXNzdXJlZCBJRCBSb290IENBMB4XDTEzMTAyMjEyMDAwMFoXDTI4MTAyMjEy
# MDAwMFowcjELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcG
# A1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTExMC8GA1UEAxMoRGlnaUNlcnQgU0hBMiBB
# c3N1cmVkIElEIENvZGUgU2lnbmluZyBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEP
# ADCCAQoCggEBAPjTsxx/DhGvZ3cH0wsxSRnP0PtFmbE620T1f+Wondsy13Hqdp0F
# LreP+pJDwKX5idQ3Gde2qvCchqXYJawOeSg6funRZ9PG+yknx9N7I5TkkSOWkHeC
# +aGEI2YSVDNQdLEoJrskacLCUvIUZ4qJRdQtoaPpiCwgla4cSocI3wz14k1gGL6q
# xLKucDFmM3E+rHCiq85/6XzLkqHlOzEcz+ryCuRXu0q16XTmK/5sy350OTYNkO/k
# tU6kqepqCquE86xnTrXE94zRICUj6whkPlKWwfIPEvTFjg/BougsUfdzvL2FsWKD
# c0GCB+Q4i2pzINAPZHM8np+mM6n9Gd8lk9ECAwEAAaOCAc0wggHJMBIGA1UdEwEB
# /wQIMAYBAf8CAQAwDgYDVR0PAQH/BAQDAgGGMBMGA1UdJQQMMAoGCCsGAQUFBwMD
# MHkGCCsGAQUFBwEBBG0wazAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AuZGlnaWNl
# cnQuY29tMEMGCCsGAQUFBzAChjdodHRwOi8vY2FjZXJ0cy5kaWdpY2VydC5jb20v
# RGlnaUNlcnRBc3N1cmVkSURSb290Q0EuY3J0MIGBBgNVHR8EejB4MDqgOKA2hjRo
# dHRwOi8vY3JsNC5kaWdpY2VydC5jb20vRGlnaUNlcnRBc3N1cmVkSURSb290Q0Eu
# Y3JsMDqgOKA2hjRodHRwOi8vY3JsMy5kaWdpY2VydC5jb20vRGlnaUNlcnRBc3N1
# cmVkSURSb290Q0EuY3JsME8GA1UdIARIMEYwOAYKYIZIAYb9bAACBDAqMCgGCCsG
# AQUFBwIBFhxodHRwczovL3d3dy5kaWdpY2VydC5jb20vQ1BTMAoGCGCGSAGG/WwD
# MB0GA1UdDgQWBBRaxLl7KgqjpepxA8Bg+S32ZXUOWDAfBgNVHSMEGDAWgBRF66Kv
# 9JLLgjEtUYunpyGd823IDzANBgkqhkiG9w0BAQsFAAOCAQEAPuwNWiSz8yLRFcgs
# fCUpdqgdXRwtOhrE7zBh134LYP3DPQ/Er4v97yrfIFU3sOH20ZJ1D1G0bqWOWuJe
# JIFOEKTuP3GOYw4TS63XX0R58zYUBor3nEZOXP+QsRsHDpEV+7qvtVHCjSSuJMbH
# JyqhKSgaOnEoAjwukaPAJRHinBRHoXpoaK+bp1wgXNlxsQyPu6j4xRJon89Ay0BE
# pRPw5mQMJQhCMrI2iiQC/i9yfhzXSUWW6Fkd6fp0ZGuy62ZD2rOwjNXpDd32ASDO
# mTFjPQgaGLOBm0/GkxAG/AeB+ova+YJJ92JuoVP6EpQYhS6SkepobEQysmah5xik
# mmRR7zGCAigwggIkAgEBMIGGMHIxCzAJBgNVBAYTAlVTMRUwEwYDVQQKEwxEaWdp
# Q2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5jb20xMTAvBgNVBAMTKERp
# Z2lDZXJ0IFNIQTIgQXNzdXJlZCBJRCBDb2RlIFNpZ25pbmcgQ0ECEAh2/5tRETdk
# 9i/wsas6vEwwCQYFKw4DAhoFAKB4MBgGCisGAQQBgjcCAQwxCjAIoAKAAKECgAAw
# GQYJKoZIhvcNAQkDMQwGCisGAQQBgjcCAQQwHAYKKwYBBAGCNwIBCzEOMAwGCisG
# AQQBgjcCARUwIwYJKoZIhvcNAQkEMRYEFDtQlXPlHzeUC6FLQGQAL0OQHGXqMA0G
# CSqGSIb3DQEBAQUABIIBAHZmms9xTXqP+hOxrSFRoLsUn6ZmpdpojR0xkAhpZYxu
# he/KwXpI06Jgmu5zWept7G3zS2/UFsxyj+9c3yOQ60lNMlUjRDxAPmw1fMwTZ7Hp
# m7JhsA7dqZ5BTX8k7Felo9dz9sEV2Vyl7xNU2Z4+l1i29DXpc6nu6IUfqn797mIy
# p3Jyk8TUzQVj+ZglEXGxZCTWnWpCVxQ/Se5afu0ScOr5gxrjptIIzv/IbV012Wyv
# fb91efEUt0zZyF/8uoOqU46GX2KQh0u7dFxONdt+WZ3K3lR7Chbp9yG5jUR7ZXj6
# t04WK74JhjElgWrZzuKqx+r48+J+InWxbANkKg4pMD0=
# SIG # End signature block
