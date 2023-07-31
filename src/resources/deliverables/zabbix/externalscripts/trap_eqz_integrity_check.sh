#!/bin/bash

xmlroot="<equalizerintegritycheckerresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do

  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  eqzpath=`awk -F'[<|>]' '/equalizerPath/{printf "%s", $3}' $f`
  daychecked=`awk -F'[<|>]' '/dayChecked/{printf "%s", $3}' $f`
  integritystatus=`awk -F'[<|>]' '/integrityStatus/{printf "%s", $3}' $f`
  resultsText=$(grep -Pzo '<eqconvResult>(\n|.)*(?=</eqconvResult>)' $f)
  resultsText=${resultsText//<eqconvResult>/}

  rnd=$((1 + RANDOM % 10000))

  echo "<pre><code>$resultsText</code></pre>" > /usr/share/nwacalendar/zabbix/doc/integrity_results_${customer_code}.html
  chmod 777 /usr/share/nwacalendar/zabbix/doc/integrity_results_${customer_code}.html
 
  ECHO="<html>
          <!--
            integritystatus:$integritystatus
          -->
          <table style=\"table-layout: fixed\" border=1>
            <tr>
              <td style=\"width:20px\">Day</td>
              <td style=\"width:30px\">Integrity Check</td>
            </tr>
            <tr>
              <td>$daychecked</td>
              <td>$integritystatus</td>
            </tr>
          </table>
      	  <a href=\"/calendar/zabbix/doc/integrity_results_${customer_code}.html?rnd=${rnd}\" target=\"_blank\">View data</a> 
	 </html>"
	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.eqz_integrity_check" -o "$ECHO"
echo $resultsText 
rm -rf $f
done
