@echo off
:: New Access Monitoring Agents - Transfer script
::
:: >>> Take care that this file has to be adapted for each installation
::
:: History:
:: 2019.04.09 - RSA - Creation  
:: 2019.04.11 - FRD - SCP
:: 2019.4.17  - RSA - Modification current directory CD

CD %~dp0
set server=%1
set port=%2
set login=%3
set password=%4
set file=%5
set remotefile=%6

del %file%
