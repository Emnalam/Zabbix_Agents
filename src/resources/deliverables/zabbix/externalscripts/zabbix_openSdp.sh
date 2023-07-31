#!/bin/bash

cwd=$(dirname $0)

source ${BASH_SOURCE%/*}/global_traps.sh

source ${BASH_SOURCE%/*}/${1}_variables.sh

client=$customer_servicedesk_name
product=$2
subject=$3
description=$4

dt=$(date '+%d/%m/%Y %H:%M:%S');
echo "$dt" >> /var/tmp/zabbix_opensdp.log
echo "Trying to create issue in SDP with following information:" >> /var/tmp/zabbix_opensdp.log
echo "Client: $client" >> /var/tmp/zabbix_opensdp.log
echo "Product: $product"  >> /var/tmp/zabbix_opensdp.log
echo "Subject: $subject"  >> /var/tmp/zabbix_opensdp.log
echo "description: $description"  >> /var/tmp/zabbix_opensdp.log

status=`java -jar $cwd/SdpClient.jar -url "https://sdp.newaccess.ch" -createRequest -subject "$subject" -description "$description" -site "$client" -product "$product" -priority "3 - High" -group "CS SUPPORT"`

if [ "$status" == "200" ]; then 
				echo "Status: OK"  >> /var/tmp/zabbix_opensdp.log
else
				echo "Status: $status"
fi
echo "-------"




