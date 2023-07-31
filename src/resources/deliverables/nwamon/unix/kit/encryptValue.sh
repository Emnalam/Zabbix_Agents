#!/bin/sh
#
# New Access Monitoring Agents - Encryption tool
#
# History:
# 2019.04.09 - RSA - Creation

if [ $# -eq 0 ]
  then
    echo "Usage: ./encryptValue.sh your_value"
else
        echo "Encrypting $@...."
        java -jar agents.jar encrypt "$@"
        echo "Done"
fi

