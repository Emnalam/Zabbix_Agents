#!/bin/bash

xmlroot="<apsyserrormonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  ap_Base=`awk -F'[<|>]' '/ap_Base/{printf "%s", $3}' $f`
  file=`awk -F'[<|>]' '/file/{printf "%s", $3}' $f`
  file=$(basename $file)
  type=`awk -F'[<|>]' '/type/{printf "%s", $3}' $f`
  ARR=`sed 's/\§/\n/g' <<< $type`
  for i in $ARR; do
    type=$i
  done

  description=`awk -F'[<|>]' '/description/{printf "%s", $3}' $f`
  if [ -z "$description" ];
  then
 	 description=`awk -F'[<|>]' '/agentDescription/{printf "%s", $3}' $f` 
  fi
  description=${description// /&nbsp;}
  description=${description//$'\t'/&nbsp;}
  description=${description//$'\n'/<br/>}

  ECHO="<table>
          <!--
            Host:$agent
            Execution Date:$agentDate
            AP BASE:$ap_Base
            File:$file
            Description:$description
            Type:$type
          -->
          <td style=\"margin-left:0; width:5%%;\">$file</td>
          <td style=\"margin-left:0; width:50%%; word-break: break-all\">$description</td>
          <td style=\"margin-left:0; width:8%%;\">$type</td>
        </table>"
  rm -rf $f
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.apsys_error" -o "$ECHO"
done
