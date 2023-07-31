#!/bin/bash


customer_code=waverton-johprd
customer_name="Waverton johprd Production"
customer_servicedesk_name="Waverton Investement Management Limited"
customer_zabbix=Waverton-johprd
customer_products="apsys pao sql transfer"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|85%;/oracle|85%;/oradata|85%;/apb|85%;/var|85%;@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%;@transfer;/|85%;/data|85%;/home|85%;"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}43"
apsys_dashboard_url="${url}44"
#cim_dashboard_url="${url}22"
#ebk_dashboard_url="${url}"
#eqz_dashboard_url="${url}8"
#las_dashboard_url="${url}10"
#integrator_dashboard_url="${url}9"
pao_dashboard_url="${url}45"
sql_dashboard_url="${url}68"
transfer_dashboard_url="${url}72"
