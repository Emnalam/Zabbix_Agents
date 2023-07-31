#!/bin/bash

source ${BASH_SOURCE%/*}/global_traps.sh

ECHO="<html>
         <head>
           <style>
             .ok{
               width: 100px;
               height: 100px;
               Background-color: green;
               text-align: center;
               font-weight: bold;
               color:black;
               float:left;
               margin-right:20px
             }
             .warning{
               width: 100px;
               height: 100px;
               Background-color: Orange;
               text-align: center;
               font-weight: bold;
               color:black;
               float:left;
               margin-right:20px
             }

             .problem{
               width: 100px;
               height: 100px;
               Background-color: red;
               text-align: center;
               font-weight: bold;
               color:black;
               float:left;
               margin-right:20px
             }
           </style>
         </head>"
for i in $(find /tmp -type f -name "*.status" -printf '%p\n' 2>/dev/null | sort -t '\0' -n); do
  client=`basename ${i##*/} .status`
  if [ -f ${BASH_SOURCE%/*}/${client}_variables.sh ];
  then    
  	source ${BASH_SOURCE%/*}/${client}_variables.sh 
  	status=`cat $i`
  	ECHO="$ECHO <a href='${customer_dashboad_url}'><div class='$status'>${customer_name}</div></a>"
  fi
done

ECHO="$ECHO </html>"
zabbix_sender -z 127.0.0.1 -s "127.0.0.1" -k "trap.all_customers" -o "$ECHO"
