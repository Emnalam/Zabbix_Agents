#!/bin/bash


customer_code=bendura
customer_name="Bendura Bank UAT"
customer_zabbix="Bendura"
customer_servicedesk_name="Bendura Bank AG"

customer_products="apsys cim ebk eqz las integrator pao sql"

# Disks Threshold
DT="@apsys;/apsys|80%;/oracle|70%;/oradata|20%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=10


customer_dashboad_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=25"
agents_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=26"
apsys_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=27"
cim_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=29"
bf_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=28"
ebk_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=42"
eqz_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=81"
las_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=32"
integrator_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=31"
pao_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=65"
sql_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=66"
