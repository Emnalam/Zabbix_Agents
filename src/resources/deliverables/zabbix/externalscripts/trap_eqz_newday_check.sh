#!/bin/bash

xmlroot="<equalizernewdaycheckerresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  eqzpath=`awk -F'[<|>]' '/equalizerPath/{printf "%s", $3}' $f`
  dayinfo=`awk -F'[<|>]' '/dayInfo/{printf "%s", $3}' $f`
  daystatus=`awk -F'[<|>]' '/dayStatus/{printf "%s", $3}' $f`

  ECHO="<html>
          <!--
            daystatus:$daystatus
          -->
          <table style=\"table-layout: fixed\" border=1>
            <tr>
              <td style=\"width:20px\">Day</td>
              <td style=\"width:30px\">Status</td>
            </tr>
            <tr>
              <td>$dayinfo</td>
              <td>$daystatus</td>
            </tr>
          </table>
        </html>"
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.eqz_newday_check" -o "$ECHO"
  rm -rf $f
done
