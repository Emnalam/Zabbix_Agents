#!/bin/bash

xmlroot="<checkcertificateexpiryresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  url=`awk -F'[<|>]' '/url/{printf "%s", $3}' $f`
  file=`awk -F'[<|>]' '/file/{printf "%s", $3}' $f` 

  if [ -z $file ]; then
   file=N/A
  fi
  commonNames=()
  infos=()  
  commonNamesXml=$(sed -n '\|<cn>|{:n;\|</cn>|!{N;bn};y|\n| |;p}' $f)
  infosXml=$(sed -n '\|<daysLeft>|{:n;\|</daysLeft>|!{N;bn};y|\n| |;p}' $f)
  subProduct=`awk -F'[<|>]' '/subproduct/{printf "%s", $3}' $f` 

  commonNamesXml=${commonNamesXml// /&nbsp;}
  infosXml=${infosXml// /&nbsp;}

  IFS=$'\n' read -rd '' -a y <<<"$infosXml"
  IFS=$'\n' read -rd '' -a y <<<"$commonNamesXml"

  for a in $commonNamesXml; do
    commonNameItem=$(echo $a | awk -F'[<|>]' '/cn/{printf "%s", $3}')
    commonNames+=($commonNameItem)
  done

  for a in $infosXml; do
    infoItem=$(echo $a | awk -F'[<|>]' '/daysLeft/{printf "%s", $3}')
    infos+=($infoItem)
  done

  warning=no
  error=no
    
  ECHO="<html>"

  for i in "${!infos[@]}"; do
    if [ ${infos[$i]} -lt 60 ] && [ ${infos[$i]} -ge 1 ]; then 
	warning=yes
    fi 
    if [ ${infos[$i]} -le 0 ]; then
        error=yes
    fi 
  done

  ECHO="$ECHO <!--"
    ECHO="$ECHO warning:$warning, error:$error"
  ECHO="$ECHO -->"

  ECHO="$ECHO <table style='width:100%'>"

  ECHO="$ECHO <tr><td colspan=2><b>url</b>: <a href='$url' target='_blank'>$url</a><td><td>&nbsp;</td></tr>"
  ECHO="$ECHO <tr><td colspan=2><b>file</b>: $file</td><td>&nbsp;</td></tr>" 
  for i in "${!commonNames[@]}"; do
      ECHO="$ECHO <tr>
                         <td style='width:5%;white-space:nowrap'>${infos[$i]} day(s) left</td>
                         <td>${commonNames[$i]}</td>  
	      </tr>"
  done
  ECHO="$ECHO </table>"
  ECHO="$ECHO </html>"

  if [ "$subProduct" != "" ]; then
     subProduct="_$subProduct"
  fi
  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.certificates$subProduct" -o "$ECHO"
 rm -f $f

done
