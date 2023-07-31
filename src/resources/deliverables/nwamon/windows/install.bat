@echo off
:: New Access Monitoring Agents - Windows Installation script
::
:: History:
:: 2019.04.09 - FRD - Creation  
::

powershell $A=\"%cd%\" ; Start-Process -Verb runAs -FilePath "powershell" -ArgumentList \"-WindowStyle Hidden $A\install.ps1\"