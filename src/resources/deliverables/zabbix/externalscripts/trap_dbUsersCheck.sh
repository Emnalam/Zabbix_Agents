#!/bin/bash

xmlroot="<servicedbusercheckresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  file=`basename $f`
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  error=`awk -F'[<|>]' '/errors/{printf "%s", $3}' $f`
  if [ $error -gt 0 ]; then error="yes"; else error="no"; fi
  warning=`awk -F'[<|>]' '/warnings/{printf "%s", $3}' $f`
  if [ $warning -gt 0 ]; then warning="yes"; else warning="no"; fi
  errors=""
  U=""
  for u in `grep -oP "(?<=<name>)[^<]+|(?<=<status>)[^<]+" $f`
  do 
    if [ -z "$U" ]; then 
      U=$u; S=""; 
    else 
      S=$u
      if [ ! -z "$errors" ]; then
        errors="$errors||"
      fi
      errors="$errors$U|$S"
      U=""
    fi
  done
  errors=`echo "<tr><td>"${errors//$'||'/"</td></tr><tr><td>"}"</td></tr>"|sed 's/|/<\/td><td>/g'`

  ECHO="<html>
          <!--
            Host:$agent
            Execution Date:$agentDate
            File:$file
            warning:$warning
            error:$error
          -->
           
          <table style=\"table-layout: fixed\" border=1 width=300>
            <tr>
              <td style=\"width:10px\">User Name</td>
              <td style=\"width:10px\">User Status</td>
            </tr>
            $errors
          </table>
        </html>"
#rm -rf $f

zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.dbUsersCheck" -o "$ECHO"

done

