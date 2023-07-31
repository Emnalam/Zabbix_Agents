#!/bin/bash

xmlroot="<integratorcommonslogmonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  errorcount=`awk -F'[<|>]' '/commonsLogcount/{printf "%s", $3}' $f`
  errors=`awk -F'[<|>]' '/commonsLogResult/{printf "%s", $3}' $f`
  errors=${errors//$'\t'/&nbsp;&nbsp;&nbsp;&nbsp;}
  errors=${errors// /&nbsp;}
  errors=${errors//@@@@/<br/>}
  
  onlywarnings=true
  
  if [[ $errors == *"Error"* ]]; then
  	onlywarnings=false;
  fi
  
  if [[ $errors == *"Fatal"* ]]; then
    onlywarnings=false;
  fi
  
  ECHO="<html>
          <table style=\"table-layout: fixed\" border=1>
            <tr>
              <td style=\"width:1024px;word-wrap: break-word\">$errors</td>
            </tr>
          </table>
        </html>" 
  rm -rf $f
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.integrator_error" -o "$ECHO"
done
