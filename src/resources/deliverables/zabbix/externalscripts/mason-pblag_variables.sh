#!/bin/bash


customer_code=mason-pblag
customer_name="Mason Privatbank Liechtenstein AG Production"
customer_servicedesk_name="Mason PB Lie AG"
customer_zabbix=Mason-PBLAG
customer_products="apsys cim ebk eqz integrator pao las sql"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|85%;/oracle|85%;/oradata|85%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}24"
apsys_dashboard_url="${url}25"
cim_dashboard_url="${url}26"
ebk_dashboard_url="${url}52"
eqz_dashboard_url="${url}50"
las_dashboard_url="${url}63"
integrator_dashboard_url="${url}51"
pao_dashboard_url="${url}27"
sql_dashboard_url="${url}64"
