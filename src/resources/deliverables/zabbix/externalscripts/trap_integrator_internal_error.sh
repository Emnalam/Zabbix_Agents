#!/bin/bash

xmlroot="<logfilesmonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh
for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  type=`awk -F'[<|>]' '/agentType/{printf "%s", $3}' $f`
  description=`awk '/<agentDescription>/,/<\/agentDescription>/' $f`
  description=${description// /&nbsp;}
  description=${description//$'\t'/&nbsp;}
  description=${description//$'\n'/<br/>}
  description=${description//|/&nbsp;&nbsp;&nbsp;&nbsp;}  
  
   ECHO="<html>
          <table style=\"table-layout: fixed\" border=1>
            <tr>
              <td style=\"width:1024px;word-wrap: break-word\">$description</td>
            </tr>
          </table>
        </html>"



rm -rf $f
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.integrator_error" -o "$ECHO"
  
  done
