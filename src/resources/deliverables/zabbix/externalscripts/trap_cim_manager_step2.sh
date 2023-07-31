#!/bin/bash

xmlroot="<cimprocessmanagermonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

output=""
error1=""
error2=""
for f in ${ftp_file_pattern}; do
  # The file is used by 3 different steps.
  # We break when not on the second one
  step=`echo $(basename $f) | head -c 5`
  if [ "$step" != "step2" ]; then
    continue
  fi

  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  errorProcess=`awk -F'[<|>]' '/errorProcess/{printf "%s", $3}' $f`
  errorThread=`awk -F'[<|>]' '/errorThread/{printf "%s", $3}' $f`

  if [ ! -z "$errorProcess" ]; then
    output="<table><tr><td>Process availability</td><td>Plugins availability</td></tr>"
    output+="<tr><td>$errorProcess</td><td>$errorThread</td></tr>"
    output+="</table>"

    error1=$errorThread
    error2=$errorProcess
  fi

  filename=step3_$(basename $f)
  path=$(dirname $f)
  mv $f $path/$filename
done

if [ ! -z "$output" ]; then
  cim_status="OK"
  if [ "$error1" != "OK" ]; then
    cim_status="PROBLEM"
  fi

  if [ "$error2" != "OK" ]; then
    cim_status="PROBLEM"
  fi

  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.cim_manager_step2" -o "$output"
fi
