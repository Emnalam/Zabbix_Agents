#!/bin/bash


customer_code=bam_prod
customer_name="BankMed Production"
customer_servicedesk_name="BankMed Suisse SA"
customer_zabbix="BankMed Production"
customer_products="apsys ebk eqz integrator las cim pao transfer"

CLIENT=$customer_zabbix

# Disks Threshold
DT="@apsys;/apsys|85%;/oracle|85%;/oradata|85%;/apb|85%@cim;C:\|95%;E:\|95%;@eqz;C:\|95%;@ebk;C:\|95%;@las;C:\|95%;E:\|95%;@pao;C:\|95%;@sql;C:\|95%;E:\|95%;@transfer;/|85%;/data|85%;/home|85%;"

# Minimum idle activity in seconds from agents before raising an alert
MIN_IDLE=15

url="https://${zabbix_server}/zabbix/zabbix.php?action=dashboard.view&dashboardid="
customer_dashboad_url="${url}74"
apsys_dashboard_url="${url}73"
transfer_dashboard_url="${url}75"
cim_dashboard_url="${url}79"
ebk_dashboard_url="${url}80"
eqz_dashboard_url="${url}81"
las_dashboard_url="${url}83"
integrator_dashboard_url="${url}84"
pao_dashboard_url="${url}82"
sql_dashboard_url="${url}76"
