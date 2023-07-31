#!/bin/bash

client=$1
product=$2
message=$3
severity=$4


wget -q "http://ch-gbc-zabbix/calendar/notification/notify.php?key=1234&client=$client&product=$product&severity=$severity&message=$message" > /dev/null
echo $?
exit $?
