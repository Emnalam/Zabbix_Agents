#!/bin/bash


customer_code=bendura_uat
customer_name="Bendura Bank UAT"
customer_zabbix="Bendura UAT"
customer_servicedesk_name="Bendura Bank AG"

customer_products="apsys cim ebk las pao sql"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|80%;/oracle|85%;/oradata|85%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15


url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}4"
#agents_dashboard_url="${url}7"
apsys_dashboard_url="${url}5"
cim_dashboard_url="${url}6"
#bf_dashboard_url="${url}"
ebk_dashboard_url="${url}7"
eqz_dashboard_url="${url}8"
las_dashboard_url="${url}10"
integrator_dashboard_url="${url}9"
pao_dashboard_url="${url}11"
sql_dashboard_url="${url}12"
