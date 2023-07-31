#!/bin/bash

xmlroot="<checkurlavailabilityresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  subProduct=`awk -F'[<|>]' '/subproduct/{printf "%s", $3}' $f`
	code=" (`awk -F'[<|>]' '/httpCode/{printf "%s", $3}' $f`)"
  url=`awk -F'[<|>]' '/url/{printf "%s", $3}' $f`
  httpStatus=`awk -F'[<|>]' '/status/{printf "%s", $3}' $f`
  
  #echo "-----------------------------------------" >> /usr/lib/zabbix/externalscripts/xx
  #echo "Services" >> /usr/lib/zabbix/externalscripts/xx
  #echo "Fichier = $f" >> /usr/lib/zabbix/externalscripts/xx
  #echo "Client = ${1}" >> /usr/lib/zabbix/externalscripts/xx
  #echo "Produit = ${2}" >> /usr/lib/zabbix/externalscripts/xx
  #echo "SubProduct = $subProduct" >> /usr/lib/zabbix/externalscripts/xx
  #echo "-----------------------------------------" >> /usr/lib/zabbix/externalscripts/xx

  error="no"

  if [ "$httpStatus" != "OK" ]; then
    error="yes"   
  fi

  ECHO="<html>
          <!--
            Error:$error
          -->
					<a href='$url'>$ECHO$code</a>
        </html>" 
  rm -rf $f

  if [ "$subProduct" != "" ]; then
    subProduct="_$subProduct"
  fi
  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.url_check" -o "Error:$error"
  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.url_check$subProduct" -o "$ECHO"

done
