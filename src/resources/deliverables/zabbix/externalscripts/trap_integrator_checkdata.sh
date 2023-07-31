#!/bin/bash

xmlroot="<sqlquerymonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

ftp_file_pattern=$(grep -l "<subproduct>checkdata</subproduct>" $ftp_file_pattern xx 2>/dev/null)

for f in ${ftp_file_pattern}; do
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

	hasDiff=0

  for a in $resultsXml; do
    categoryItem=$(echo $a | awk -F'[<|>]' '/Category/{printf "%s", $3}')
		if [ "$categoryItem" = "ptf" ]; then
						categoryItem="Portfolios"
		fi
		 if [ "$categoryItem" = "pos" ]; then
            categoryItem="Positions"
    fi
    countDiffItem=$(echo $a | awk -F'[<|>]' '/CountDiff/{printf "%s", $7}')
    dataDiffItem=$(echo $a | awk -F'[<|>]' '/DataDiff/{printf "%s", $11}')
    current_data=$current_data$categoryItem$countDiffItem$dataDiffItem
    resultItem=$categoryItem$';'$countDiffItem$';'$dataDiffItem
    results+=($resultItem)

		if [ "$countDiffItem" != "ok" ]; then
						hasDiff=1
		fi

		if [ "$dataDiffItem" != "0" ]; then
						hasDiff=1
		fi

  done


  ECHO="<html>"

  ECHO="$ECHO <!--"
  ECHO="$ECHO hasDiff:$hasDiff"
  ECHO="$ECHO -->"


  ECHO="$ECHO <table>"
  ECHO="$ECHO <tr><td>Category</td><td>Total items differences</td><td>Data differences</td></tr>"
  for i in "${!results[@]}"; do
    ECHO="$ECHO <tr>"
    elements=${results[$i]//;/ }
    for j in $elements; do
      ECHO="$ECHO <td align='center'>$j</td>"
    done
    ECHO="$ECHO <tr>"
  done
  ECHO="$ECHO </table>"
  ECHO="$ECHO </html>"

	zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.integrator_checkdata" -o "$ECHO"
  rm $f
done
