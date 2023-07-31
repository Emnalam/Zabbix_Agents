#!/bin/bash


customer_code=tellco-ag
customer_name="Tellco AG Production"
customer_servicedesk_name="Tellco AG"
customer_zabbix=Tellco-AG
customer_products="apsys pao sql transfer"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|85%;/oracle|95%;/oradata|90%;/apb|85%;/var|85%;C:\|95%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%;@transfer;/|85%;/data|85%;/home|85%;"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}29"
apsys_dashboard_url="${url}30"
#cim_dashboard_url="${url}22"
#ebk_dashboard_url="${url}"
#eqz_dashboard_url="${url}8"
#las_dashboard_url="${url}10"
#integrator_dashboard_url="${url}9"
pao_dashboard_url="${url}31"
sql_dashboard_url="${url}66"
transfer_dashboard_url="${url}71"
