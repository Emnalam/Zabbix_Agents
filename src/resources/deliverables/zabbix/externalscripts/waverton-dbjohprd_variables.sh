#!/bin/bash
 

customer_code=waverton-dbjohprd
customer_name="Waverton dbjohprd Production"
customer_servicedesk_name="Waverton Investement Management Limited"
customer_zabbix=Waverton-dbjohprd
customer_products="apsys sql"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|85%;/oracle|85%;/oradata|85%;/apb|85%;/var|85%;@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}41"
apsys_dashboard_url="${url}42"
#cim_dashboard_url="${url}22"
#ebk_dashboard_url="${url}"
#eqz_dashboard_url="${url}8"
#las_dashboard_url="${url}10"
#integrator_dashboard_url="${url}9"
#pao_dashboard_url="${url}46"
sql_dashboard_url="${url}67"
