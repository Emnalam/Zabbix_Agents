#!/bin/bash


customer_code=cim-banque
customer_name="CIM Banque"
customer_zabbix="CIM Banque"
customer_servicedesk_name="CIM Banque"

customer_products="apsys pao sql"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|90%;/oracle|90%;/oradata|90%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|90%;@sql;C:\|95%;E:\|95%"
# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}56"
#agents_dashboard_url="${url}67"
apsys_dashboard_url="${url}57"
#cim_dashboard_url="${url}34"
#bf_dashboard_url="${url}35"
#ebk_dashboard_url="${url}35"
#eqz_dashboard_url="${url}36"
#las_dashboard_url="${url}38"
#integrator_dashboard_url="${url}37"
pao_dashboard_url="${url}58"
sql_dashboard_url="${url}61"
