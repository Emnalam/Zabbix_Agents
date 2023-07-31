#!/bin/bash

source ${BASH_SOURCE%/*}/global_traps.sh
source ${BASH_SOURCE%/*}/${1}_variables.sh

status() {
  L="Unknown"
	case "$1" in
        "agents") P="Agents";         L=$P ;;
         "apsys") P="Apsys";          L=$P ;;
           "cim") P="CIM";            L=$P ;;
    "integrator") P="Integrator";     L=$P ;;
           "eqz") P="Equalizer";      L=$P ;;
           "las") P="Logical Access"; L="Logical&nbsp;Access" ;;
           "ebk") P="Ebanking";       L="EBanking" ;;
            "bf") P="Bankers Front";  L="Bankers&nbsp;Front" ;;
           "pao") P="PAO";            L=$P ;;
           "sql") P="SQL";            L=$P ;;
      "transfer") P="Transfer";       L=$P ;;
  esac
	st=`$DIR/getProblems.py -c "$customer_zabbix" -p "$P" -u "$zabbix_api_url" -lu "$zabbix_api_username" -lp "$zabbix_api_password"`
#	echo $st
	rs="ok"
  if [ "$st" = "WARNING" ]; then
    rs="warning";
		if [ "$GP" != "problem" ]; then GP=$rs; fi
  else if [ "$st" = "PROBLEM" ]; then
         rs="problem";
				 GP=$rs
       fi
  fi
  D=${1}_dashboard_url
  ECHO="$ECHO
        	<a href='${!D}'><div class='$rs$statusStyle'>$L</div></a>"
}

new_line() {
	ECHO="$ECHO
					<br/>"
}
