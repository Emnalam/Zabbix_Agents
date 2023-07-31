#!/bin/bash

last_version=1.1.8.3.1
ftp_dir=/home/nwaagent/inputs
zabbix_server=atrprod301.taurus.intern

zabbix_api_url=http://localhost/zabbix/api_jsonrpc.php
zabbix_api_username=nwaagent
zabbix_api_password=2+usaJ7#9E

tmp_dir=/var/tmp
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

#customers="bendura_prod hottinger-ag mason-pblag oddo-bhf tellco-ag waverton-dbjohprd waverton-johprd cim-banque"
customers="bam_prod bendura_uat bendura_prod hottinger-ag mason-pblag oddo-bhf tellco-ag waverton-dbjohprd waverton-johprd cim-banque"

all_products="apsys cim ebk eqz las integrator pao sql transfer"
all_customers_url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid=3"
morning_dashboard_url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid=77"

if [ $# -eq 0 ]; then
	echo "ERROR: Cannot run script without parameters. You need to specify at least the customer code"
	exit 1
fi

if [ $# -eq 1 ]; then
	if [ "${1}" != "global" ]; then
		ftp_file_pattern=$(grep -l "$xmlroot" $ftp_dir/${1}/ -R 2>/dev/null)
	fi
fi

if [ $# -eq 2 ]; then
	ftp_file_pattern=$(grep -l "<product>${2}</product>" dummy $(grep -l "$xmlroot" $ftp_dir/${1}/* 2>/dev/null) xx 2>/dev/null)
fi
if [ $# -eq 3 ]; then
	ftp_file_pattern=$(grep -l "<subproduct>${3}</subproduct>" dummy $(grep -l "<product>${2}</product>" dummy $(grep -l "$xmlroot" $ftp_dir/${1}/* 2>/dev/null)  xx 2>/dev/null) xx 2>/dev/null) 
fi
case "$2" in
  'apsys')      PRODUCT=Apsys
                ;;
  'pao')        PRODUCT=PAO
                ;;
  'cim')        PRODUCT=CIM
                ;;
  'eqz')        PRODUCT=Equalizer
                ;;
  'integrator') PRODUCT=Integrator
                ;;
  'las')        PRODUCT="Logical Access"
                ;;
  'ebk')        PRODUCT=Ebanking
                ;;
  'sql')        PRODUCT=SQL
                ;;
  'transfer')   PRODUCT=Transfer
                ;;
  *)            PRODUCT=Unknown
                ;;
esac
