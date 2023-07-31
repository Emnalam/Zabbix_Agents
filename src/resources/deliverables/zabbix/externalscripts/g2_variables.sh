#!/bin/bash


customer_code=g2
customer_name="G2"
customer_zabbix="G2"
customer_servicedesk_name="G2 Reference environment"

customer_products="apsys ebk eqz integrator pao"

# Disks Threshold
#DT="@apsys;/apsys|80%;/oracle|70%;/oradata|20%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%"
#DT="@apsys;/dev/lvapsys|80%;/dev/lvoracle|70%;/dev/lvoradata|80%;/dev/lvapb|85%;@eqz;C:\|95%;D:\|90%;@pao;C:\|95%;D:\|90%"
DT="@apsys;/apsys|80%;/oracle|70%;/oradata|80%;/apb|85%;@eqz;C:\|95%;D:\|90%;@pao;C:\|95%;D:\|90%"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=10


customer_dashboad_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=83"
#agents_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=26"
apsys_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=84"
#cim_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=29"
#bf_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=28"
ebk_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=88"
eqz_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=86"
#las_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=32"
integrator_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=87"
pao_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=85"
#sql_dashboard_url="http://ch-gbc-zabbix/zabbix/zabbix.php?action=dashboard.view&dashboardid=66"
