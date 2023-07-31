@echo off
:: New Access Monitoring Agents - Windows Scheduler script
::
:: History:
:: 2019.04.08 - RSA - Creation  
:: 2019.04.09 - FRD - Powershell integration  
::

powershell -NoLogo -WindowStyle Hidden .\nwamon.ps1 -kill
timeout 5
powershell -NoLogo -WindowStyle Hidden .\nwamon.ps1 -run
