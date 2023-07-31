#!/bin/sh
#
# New Access Monitoring Agents - Oracle Zabbix user creation
#
# History:
# 2019.04.09 - FRD - Creation
# 2019.04.23 - FRD - Apsys/CIM version

trap "sh_abort 1 Receive a CTRL-C" 2
# -----------------------------------------------------------------------------
. $(which apblibcust.sh)

echo Execute $(basename $0)
zabbix_schema_logfile=${zabbix_schema_logfile:-$HOME/apblog/zabbix_schema_${zabbix_schema_dbname}_$(date +%Y%m%d%H%M%S).log}
# -----------------------------------------------------------------------------
sh_echo "- Execute: zabbix_schema.sh -dbname=${zabbix_schema_dbname} -dbhost=${zabbix_schema_dbhost} -host=$(hostname) -logfile=${zabbix_schema_logfile}"

sh_echo " - Create user zabbix"
cust_sqlplus -dbname=${zabbix_schema_dbname} -dbhost=${zabbix_schema_dbhost} '
  whenever sqlerror continue
  drop user zabbix cascade;
  create user zabbix identified by "zabb1x_1234%" default tablespace users temporary tablespace temp;
  grant create session to zabbix;
  alter user zabbix quota unlimited on users;
  revoke select any table from zabbix;
  grant select on sys.dba_data_files to zabbix;
  grant select on sys.v_$instance to zabbix;
  grant select on sys.v_$rman_status to zabbix;
  grant SELECT_CATALOG_ROLE to zabbix;

' >> ${zabbix_schema_logfile}
sh_error $? "Check logfile"
