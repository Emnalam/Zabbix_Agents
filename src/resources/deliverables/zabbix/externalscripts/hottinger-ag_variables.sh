#!/bin/bash


customer_code=hottinger-ag
customer_name="Hottinger AG Production"
customer_servicedesk_name="Hottinger AG"
customer_zabbix=Hottinger-AG
customer_products="apsys cim pao sql transfer"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|85%;/oracle|85%;/oradata|85%;/apb|85%;/var|85%;@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%;@transfer;/|85%;/data|85%;/home|85%;"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}20"
apsys_dashboard_url="${url}21"
cim_dashboard_url="${url}22"
#ebk_dashboard_url="${url}"
#eqz_dashboard_url="${url}8"
#las_dashboard_url="${url}10"
#integrator_dashboard_url="${url}9"
pao_dashboard_url="${url}23"
sql_dashboard_url="${url}65"
transfer_dashboard_url="${url}69"
