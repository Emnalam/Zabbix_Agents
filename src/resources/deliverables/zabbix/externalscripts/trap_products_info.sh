#!/bin/bash

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh
source ${BASH_SOURCE%/*}/products_status.sh

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
                margin-right:20px;
              }
              .warning{
                width: 100px;
                height: 100px;
                Background-color: Orange;
								text-align: center;
								font-weight: bold;
                color:black;
                float:left;
                margin-right:20px;
              }
              .problem{
                width: 100px;
                height: 100px;
                Background-color: red;
								text-align: center;
								font-weight: bold;
                color:black;
                float:left;
                margin-right:20px;
              }
            </style>
          </head>
          <body>
            <div style='width:100%%'>
"

for p in `echo $customer_products`; do
  status $p
done

ECHO="$ECHO
      </div>
          </body>
        </html>
"
zabbix_sender -z 127.0.0.1 -s "$CLIENT" -k "trap.Products_Info" -o "$ECHO"
