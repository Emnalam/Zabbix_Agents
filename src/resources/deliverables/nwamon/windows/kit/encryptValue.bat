@echo off
::
:: New Access Monitoring Agents - Encryption tool
::
:: History:
:: 2019.04.09 - RSA - Creation

if "%1"=="" goto :done

echo Encrypting %1...
java -jar agents.jar encrypt "%1"
goto :end

:done
echo "Usage: encryptValue.bat your_value"

:end
echo Done
