#!/bin/bash

xmlroot="<servicesmonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  current_data=""
  processes=()
  messages=()  
  processesXml=$(sed -n '\|<process>|{:n;\|</process>|!{N;bn};y|\n| |;p}' $f)
  messagesXml=$(sed -n '\|<message>|{:n;\|</message>|!{N;bn};y|\n| |;p}' $f)
	subProduct=`awk -F'[<|>]' '/subproduct/{printf "%s", $3}' $f` 

  processesXml=${processesXml// /&nbsp;}


  IFS=$'\n' read -rd '' -a y <<<"$processesXml"
  IFS=$'\n' read -rd '' -a y <<<"$messagesXml"

  for a in $processesXml; do
    processItem=$(echo $a | awk -F'[<|>]' '/process/{printf "%s", $3}')
    processes+=($processItem)
    current_data=$current_data$processItem
  done

  for a in $messagesXml; do
    messageItem=$(echo $a | awk -F'[<|>]' '/message/{printf "%s", $3}')
    messages+=($messageItem)
    current_data=$current_data$messageItem
  done

	if [ ${2} == "off" ] ; then
	  echo "-----------------------------------------" >> /usr/lib/zabbix/externalscripts/xx
	  echo "Services" >> /usr/lib/zabbix/externalscripts/xx
	  echo "Fichier = $f" >> /usr/lib/zabbix/externalscripts/xx
	  echo "Client = ${1}" >> /usr/lib/zabbix/externalscripts/xx
	  echo "Produit = ${2}" >> /usr/lib/zabbix/externalscripts/xx
	  echo "Processes = $processes" >> /usr/lib/zabbix/externalscripts/xx
	  echo "messageItem = $messageItem" >> /usr/lib/zabbix/externalscripts/xx
	  echo "-----------------------------------------" >> /usr/lib/zabbix/externalscripts/xx
	fi

  idle=1
    
  ECHO="<html>"

  ECHO="$ECHO <!--"
  for i in "${!messages[@]}"; do
    ECHO="$ECHO message:${messages[$i]}"
  done
  ECHO="$ECHO -->"


  ECHO="$ECHO <table>"
  for i in "${!processes[@]}"; do
    if [ "${messages[$i]}" = "NotFound" ]; then
      ECHO="$ECHO <tr>
                                      <td>${processes[$i]}</td>
                              <td><img width=\"10px\" src=\"/calendar/zabbix/images/service_notfound.png\"/></td>
                              </tr>"
    else
      ECHO="$ECHO <tr>
                                      <td>${processes[$i]}</td>
                              <td><img width=\"10px\" src=\"/calendar/zabbix/images/service_running.png\"/></td>
                              </tr>"
    fi
  done
  ECHO="$ECHO </table>"
  ECHO="$ECHO </html>"

	if [ "$subProduct" != "" ]; then
	  subProduct="_$subProduct"
	fi
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.ServicesMonitor$subProduct" -o "$ECHO"

	rm -f $f

done
