#!/bin/bash

xmlroot="<commandexecutorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

ftp_file_pattern=$(grep -l "<subproduct>autoarc</subproduct>" $ftp_file_pattern xx 2>/dev/null)

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  commandOutput=`awk -F'[<|>]' '/commandOutput/{printf "%s", $3}' $f`
  commandOutput=${commandOutput// /&nbsp;}
  commandOutput=${commandOutput//$'\t'/&nbsp;}
  commandOutput=${commandOutput//$'\n'/<br/>}
  
  if [ "$commandOutput" = "" ]; then
    ECHO="<html>
           <!--
             AUTOARC_IDLE:0
           -->
           <table style=\"table-layout: fixed\" border=1 width=350>
             <tr>
                <td style=\"width:340px\">AutoArchiving Idle files</td>
              </tr>
              <tr>
                <td>No Files are idle</td>
              </tr>
            </table>
          </html>"
  
  else
    ECHO="<html>
            <!--
              AUTOARC_IDLE:1
            -->
            <table style=\"table-layout: fixed\" border=1 width=350>
              <tr>
                <td style=\"width:340px\">AutoArchiving Idle files</td>
              </tr>
              <tr>
                <td>Some files are idle for more than 5 minutes</td>
              </tr>
            </table>
          </html>" 
  
  fi
	echo $ECHO
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.las_autoarc_idle" -o "$ECHO"
	rm -rf $f
done
