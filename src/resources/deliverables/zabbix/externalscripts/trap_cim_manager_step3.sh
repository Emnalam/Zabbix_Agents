#!/bin/bash

xmlroot="<cimprocessmanagermonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

output=""
for f in ${ftp_file_pattern}; do
  # The file is used by 3 different steps.
  # We break when not on the third one
  step=`echo $(basename $f) | head -c 5`
  if [ "$step" != "step3" ]; then
    continue
  fi

  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  errorChecker=`awk -F'[<|>]' '/errorChecker/{printf "%s", $3}' $f`
  errorPluginName=`awk -F'[<|>]' '/errorPluginName/{printf "%s", $3}' $f`
  errorLog=`awk -F'[<|>]' '/errorLog/{printf "%s", $3}' $f`

  if [ ! -z "$errorPluginName" ]; then
    errorPluginName=${errorPluginName// /&nbsp;}
    errorPluginName=${errorPluginName//$'\n'/<br/>}

    if [ -z "$errorChecker" ]; then
      error=$errorLog
    else
      error=$errorChecker
    fi

    error=${error// /&nbsp;}
    error=${error//$'\t'/&nbsp;}
    error=${error//;;;;NewLine;;;;/<br/>}

		output+="<!--"
    output+="  error:yes" 
    output+=" -->"

    output+="   <table style=\"table-layout: fixed\" border=0 width=868>"
    output+="         <tr><td width=\"150\">$errorPluginName</td><td style=\"word-wrap: break-word\">$error</td></tr>"
    output+="  </table>"
  fi
 rm -rf $f
done
echo $output
if [ ! -z "$output" ]; then
  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.cim_manager_step3" -o "$output"
fi
