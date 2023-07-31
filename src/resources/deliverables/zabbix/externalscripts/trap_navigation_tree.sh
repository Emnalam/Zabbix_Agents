#!/bin/bash

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh
source ${BASH_SOURCE%/*}/products_status.sh

statusStyle='navproduct'

GECHO="<html>
        <head>
          <style>
            .oknav {
              width: 100px;
              height: 70px;
              Background-color: green;
              text-align: center;
              font-weight: bold;
              color:black;
              margin-bottom:20px
            }

            .warningnav {
              width: 100px;
              height: 70px;
              Background-color: Orange;
              text-align: center;
              font-weight: bold;
              color:black;
              margin-bottom:20px
            }

            .problemnav {
              width: 100px;
              height: 70px;
              Background-color: red;
              text-align: center;
              font-weight: bold;
              color:black;
              margin-bottom:20px
            }

            .oknavproduct {
              width: 100px;
              height: 20px;
              Background-color: green;
              text-align: center;
              font-weight: bold;
              color:black;
            }

            .warningnavproduct {
              width: 100px;
              height: 20px;
              Background-color: Orange;
              text-align: center;
              font-weight: bold;
              color:black;
            }

            .problemnavproduct {
              width: 100px;
              height: 20px;
              Background-color: red;
              text-align: center;
              font-weight: bold;
              color:black;
            }
          </style>
        </head>"

GECHO="$GECHO <a href='$all_customers_url'>Global Dashboard</a><br><a href='$morning_dashboard_url' target='_blank'>Morning Dashboard</a><br><br>"

GP="ok"
for p in `echo $customer_products`; do
  status $p
	new_line
done
echo $GP > /tmp/${1}.status
chmod 777  /tmp/${1}.status 
customer_status="${GP}nav"
GECHO="$GECHO <a href='${customer_dashboad_url}'><div class='$customer_status'>${customer_name}</div></a>"
GECHO="$GECHO $ECHO <div style='height=60px'>&nbsp;</div><div style='height=60px'>&nbsp;</div>"

zabbix_sender -z 127.0.0.1 -s "$CLIENT" -k "trap.Navigation_tree" -o "$GECHO"
