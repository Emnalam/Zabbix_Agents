#!/bin/bash

xmlroot="<cimprocessmanagermonitorresult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

output=""
for f in ${ftp_file_pattern}; do
  # The file is used by 3 different steps.
  # We break when not on the first one
  step=`echo $(basename $f) | head -c 4`
  if [ "$step" = "step" ]; then
    continue
  fi
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  expectedThreads=`awk -F'[<|>]' '/expectedThreads/{printf "%s", $3}' $f`
  expectedPlugins=`awk -F'[<|>]' '/expectedPlugins/{printf "%s", $3}' $f`
  totalThreads=`awk -F'[<|>]' '/totalThreads/{printf "%s", $3}' $f`
  idleThreads=`awk -F'[<|>]' '/idleThreads/{printf "%s", $3}' $f`

  expectedPlugins=${expectedPlugins// /&nbsp;}
  expectedPlugins=${expectedPlugins//;;;;/<br/>}

  if [ ! -z "$expectedThreads" ]; then
    output="<table>"
    output+="<tr><td>Expected Plugins</td><td>Expected Threads</td><td>Current Threads</td><td>Idle Threads</td></tr>"
    output+="<tr><td>$expectedPlugins</td><td>$expectedThreads</td><td>$totalThreads</td><td>$idleThreads</td></tr>"
    output+="</table>"            
  fi

  # Let's go to step2
  filename=step2_$(basename $f)
  dir=$(dirname $f)
  mv $f $dir/$filename
done

if [ ! -z "$output" ]; then
  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.cim_manager_step1" -o "$output"
fi
