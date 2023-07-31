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
	host=""
	version=""
	for i in ${ftp_file_pattern}; do
	       host=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $i`
	       version=`awk -F'[<|>]' '/agent_Version/{printf "%s", $3}' $i`
	       break	
	done
	if [ -z "$version" ]; then
		echo "DataAvailable<br>$host"	
	else
		 echo "DataAvailable (v.$version)<br>$host"	
	fi 
fi

