@echo off
:: New Access Monitoring Agents - Windows Scheduler script
::
:: History:
:: 2019.04.09 - FRD - Creation  
:: 2019.04.23 - FRD - Run As Admin
::

powershell $A=\"%cd%\" ; $P=\"%1\" ; Start-Process -Verb runAs -FilePath "powershell" -ArgumentList \"-WindowStyle Hidden $A\nwamon.ps1 $P\"
