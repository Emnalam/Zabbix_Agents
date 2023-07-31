#!/bin/bash

xmlroot="<logfilesmonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

ftp_file_pattern=$(grep -l "<subproduct>eqxor</subproduct>" $ftp_file_pattern xx 2>/dev/null)

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  code=`awk -F'[<|>]' '/agentCode/{printf "%s", $3}' $f`
  file=`awk -F'[<|>]' '/file/{printf "%s", $3}' $f`
  type=`awk -F'[<|>]' '/agentType/{printf "%s", $3}' $f`
  description=`awk -F'[<|>]' '/agentDescription/{printf "%s", $3}' $f`
  description=${description// /&nbsp;}
  description=${description//$'\t'/&nbsp;}
  description=${description//$'\n'/<br/>}

  ECHO="<html>
          <!--
            Description:$description
            Code:$code
          -->
          <table style=\"table-layout: fixed\" border=1 width=\"1024\">
            <tr>
              <td style=\"width:300px\">Description</td>
              <td style=\"width:120px\">Code</td>
              <td style=\"width:550px\">File</td>
            </tr>
            <tr>
              <td style=\"word-wrap: break-word\">$description</td>
              <td>$code</td>
              <td>$file</td>
            </tr>
          </table>
        </html>"
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.eqz_eqxor_error" -o "$ECHO"
  rm -rf $f
done
