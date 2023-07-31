#!/bin/bash

xmlroot="<logfilesmonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

ftp_file_pattern=$(grep -l "<subproduct>file_progress</subproduct>" $ftp_file_pattern xx 2>/dev/null)
echo FTP $ftp_file_pattern
for f in ${ftp_file_pattern}; do
  tag=`awk -F'[<|>]' '/zTag/{printf "%s", $3}' $f` 
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  code=`awk -F'[<|>]' '/agentCode/{printf "%s", $3}' $f`
  file=$(grep -Pzo '<file>(\n|.)*(?=</file>)' $f)
  file=${file//<file>/}
  file=$(basename $file)
  type=`awk -F'[<|>]' '/agentType/{printf "%s", $3}' $f`
  description=$(grep -Pzo '<agentDescription>(\n|.)*(?=</agentDescription>)' $f)
  description=${description//<agentDescription>/} 
  description=${description// /&nbsp;}
  description=${description//$'\t'/&nbsp;}
  description=${description//$'\n'/<br/>}
  ECHO="<table>
          <!--
            Host:$agent
            Execution Date:$agentDate
            File:$file
            Code:$code
          -->
          <td style=\"margin-left:0; width:50%%; word-break: break-all\">$description</td>
        </table>"
 if [ $code != "IDLE" ]; then
 	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.file_progress_$tag" -o "$ECHO"
 fi
# zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.file_progress_${tag}_alert" -o "Code:$code"
rm -rf $f
done
