#!/bin/bash

xmlroot="<sqlquerymonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

ftp_file_pattern=$(grep -l "<subproduct>intraday</subproduct>" $ftp_file_pattern xx 2>/dev/null)

for f in ${ftp_file_pattern}; do
  diffcount=0
  results=()
  resultsXml=$(sed -n '\|<sqlQueryResult>|{:n;\|</sqlQueryResult>|!{N;bn};y|\n| |;p}' $f)

  resultsXml=${resultsXml//&lt;/<}
  resultsXml=${resultsXml//&gt;/>}
  resultsXml=${resultsXml//<sqlQueryResult>/}
  resultsXml=${resultsXml//<\/sqlQueryResult>/}
  resultsXml=${resultsXml//<Results>/}
  resultsXml=${resultsXml//<\/Results>/}
  resultsXml=${resultsXml//<Row>/$'\n'}

  IFS=$'\n' read -rd '' -a y <<<"$resultsXml"

  for a in $resultsXml; do
    check1Item=$(echo $a | awk -F'[<|>]' '/CHECK1/{printf "%s", $3}')
    check2Item=$(echo $a | awk -F'[<|>]' '/CHECK2/{printf "%s", $7}')
    check3Item=$(echo $a | awk -F'[<|>]' '/CHECK3/{printf "%s", $11}')
    check4Item=$(echo $a | awk -F'[<|>]' '/CHECK4/{printf "%s", $15}')
    current_data=$check1Item$check2Item$check3Item$check4Item
    resultItem=$check1Item$' 'N/A' '$check3Item$' '$check4Item
    results+=($resultItem)
  done

  diffcount=`awk -F'[<|>]' '/count/{printf "%s", $3}' $f`

  ECHO="<html>"

  ECHO="$ECHO <!--"
  for i in "${!results[@]}"; do
    ECHO="$ECHO result:${results[$i]}"
  done
  ECHO="$ECHO -->"


  ECHO="$ECHO <table>
                <tr><td>Check Type</td><td>Status</td></tr>
                <tr><td>cdtypc is E and cdetatc is not A</td><td>${results[0]}</td></tr>
                <tr><td>cdtypc is I and cdetatc is (D or T)</td><td>N/A</td></tr> 
		<tr><td>cdtypc is I and dttrtl is not sysdate</td><td>${results[2]}</td></tr>
                <tr><td>cdtypc is I and cdetatc is S and hrtrtl &lt; systime - 25 minutes</td><td>${results[3]}</td></tr>
              </table>
            </html>"

  rm $f
  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.integrator_intraday" -o "$ECHO"
done
