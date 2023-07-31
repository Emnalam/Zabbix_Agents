#!/bin/bash

xmlroot="<product>"

source ${BASH_SOURCE%/*}/global_traps.sh

source ${BASH_SOURCE%/*}/${1}_variables.sh

files=0
if [ "$ftp_file_pattern" != "" ]; then
	files=`find ${ftp_file_pattern} -mmin -${MIN_IDLE} -type f -print | wc -l`
fi

if [ "$files" = "0" ]; then
		echo "NoData"
else
	version=""
	for i in ${ftp_file_pattern}; do
	       version=`awk -F'[<|>]' '/agent_Version/{printf "%s", $3}' $i`
	       break	
	done
	if [ "$version" != "$last_version" ]; then
	 zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.agent_version_check" -o "NOK:$version"	
	else
         zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.agent_version_check" -o "OK"	
	fi
fi

