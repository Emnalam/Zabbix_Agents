#!/bin/bash


customer_code=bendura_prod
customer_name="Bendura Bank Production"
customer_zabbix="Bendura Production"
customer_servicedesk_name="Bendura Bank AG"

customer_products="apsys cim ebk eqz las integrator pao sql"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|80%;/oracle|70%;/oradata|20%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%"
# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}32"
#agents_dashboard_url="${url}67"
apsys_dashboard_url="${url}33"
cim_dashboard_url="${url}34"
#bf_dashboard_url="${url}35"
ebk_dashboard_url="${url}35"
eqz_dashboard_url="${url}36"
las_dashboard_url="${url}38"
integrator_dashboard_url="${url}37"
pao_dashboard_url="${url}55"
sql_dashboard_url="${url}40"
