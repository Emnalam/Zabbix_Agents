#!/bin/bash

xmlroot="<monitortransactionlogresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh


for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  ap_Base=`awk -F'[<|>]' '/ap_Base/{printf "%s", $3}' $f`
  file=`awk -F'[<|>]' '/logfile/{printf "%s", $3}' $f`
  # file=$(basename $file)
  cdApps=`awk -F'[<|>]' '/cdApps/{printf "%s", $3}' $f`
  transaction=`awk -F'[<|>]' '/transactionnumber/{printf "%s", $3}' $f`
  message=`awk -F'[<|>]' '/message/{printf "%s", $3}' $f`
  message=${message// /&nbsp;}
  #message=${message//, /&nbsp;}

  if [ -z "$file" ]; then
    ACK="<b><font color='green'>Below transactions have been auto acknowledged and processed</font></b>"
		zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.apsys_transaction" -o "$ACK"
  else
    file=$(basename $file)
    ECHO="<html>
            <!--
              Host:$agent
              Execution Date:$agentDate
              AP BASE:$ap_Base
              File:$file
              cdApps:$cdApps
              transaction:$transaction
              message:$message
              error:yes
            -->
            <table style=\"table-layout: fixed\" border=1 width=1024>
              <tr>
                <td style=\"display:none\">Host</td>
                <td style=\"display:none\">Execution Date</td>
                <td style=\"display:none\">AP BASE</td>
                <td style=\"width:100px\">File</td>
                <td style=\"width:30px\">CdApps</td>
                <td style=\"width:50px\">Transaction</td>
                <td style=\"width:250px\">Message</td>
              </tr>
              <tr>
                <td style=\"display:none\">$agent</td>
                <td style=\"display:none\">$agentDate</td>
                <td style=\"display:none\">$ap_Base</td>
                <td>$file</td>
                <td>$cdApps</td>
                <td>$transaction</td>
                <td>$message</td>
              </tr>
            </table>
          </html>"
		zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.apsys_transaction" -o "$ECHO"
  fi
  rm -rf $f   
done
