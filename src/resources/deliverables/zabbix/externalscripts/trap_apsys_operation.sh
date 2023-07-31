#!/bin/bash

xmlroot="<getopertioninforesult>"

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

for f in ${ftp_file_pattern}; do
  agent=`awk -F'[<|>]' '/agent_Host/{printf "%s", $3}' $f`
  agentDate=`awk -F'[<|>]' '/agentExecutionDate/{printf "%s", $3}' $f`
  ap_Base=`awk -F'[<|>]' '/ap_Base/{printf "%s", $3}' $f`
  accountDate=`awk -F'[<|>]' '/accountingDate/{printf "%s", $3}' $f`
  accountingSessionNumber=`awk -F'[<|>]' '/accountingSessionNumber/{printf "%s", $3}' $f`
  operatingMode=`awk -F'[<|>]' '/operatingMode/{printf "%s", $3}' $f`
  currentStage=`awk -F'[<|>]' '/currentStage/{printf "%s", $3}' $f`
  stepHasAborted=`awk -F'[<|>]' '/stepHasAborted/{printf "%s", $3}' $f`
  stepInInitializationPase=`awk -F'[<|>]' '/stepInInitializationPase/{printf "%s", $3}' $f`
  operationIs=`awk -F'[<|>]' '/operationIs/{printf "%s", $3}' $f`
  previous_data=""
  ECHO="<html>
          <!--
            Host:$agent
            Execution Date:$agentDate
            AP BASE:$ap_Base
            Accounting Date:$accountDate
            Session Number:$accountingSessionNumber
            Operating Mode:$operatingMode
            Current Stage:$currentStage
            Step Has Aborted:$stepHasAborted
            Step in Init Phase:$stepInInitializationPase
            Operation Is 24x7:$operationIs
          -->
          <table border=1>
            <tr>
              <td>Host</td>
              <td align=\"right\">$agent</td>
            </tr>
            <tr>
              <td>Execution Date</td>
              <td align=\"right\">$agentDate</td>
            </tr>
            <tr>
              <td>AP BASE</td>
              <td align=\"right\">$ap_Base</td>
            </tr>
            <tr>
              <td>Accounting Date</td>
              <td align=\"right\">$accountDate</td>
            </tr>
            <tr>
              <td>Session Number</td>
              <td align=\"right\">$accountingSessionNumber</td>
            </tr>
            <tr>
              <td>Operating Mode</td>
              <td align=\"right\">$operatingMode</td>
            </tr>
            <tr>
              <td>Current Stage</td>
              <td align=\"right\">$currentStage</td>
            </tr>
            <tr>
              <td>Step Has Aborted</td>
              <td align=\"right\">$stepHasAborted</td>
            </tr>
            <tr>
              <td>Step in Init Phase</td>
              <td align=\"right\">$stepInInitializationPase</td>
            </tr>
            <tr>
              <td>Operation Is 24x7</td>
              <td align=\"right\">$operationIs</td>
            </tr>
          </table>
        </html>"

  zabbix_sender -z 127.0.0.1 -s "$CLIENT - $PRODUCT" -k "trap.apsys_operation" -o "$ECHO"
  rm -rf $f

done
