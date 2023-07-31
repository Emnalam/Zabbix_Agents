@echo off

if "%~1"=="" goto blank

set process="%~dp0\install.ps1 -update -dir %1"

powershell -noexit "& %process%


:blank
echo This script is not meant to be executed manually but only by nwamon when updating automatically it's package
