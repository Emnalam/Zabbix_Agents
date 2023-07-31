#!/bin/bash

source ${BASH_SOURCE%/*}/global_traps.sh global

#if [ -f /tmp/trap.pid ]; then
#  echo "Already running..."
#	exit
#fi

if [ "$USER" != "nwaagent" ]; then
  echo "NWAAGENT user only"
	exit 1
fi

echo `date`

echo $$ > /tmp/trap.pid
echo "__________________________________________________________________________"
for C in $(echo $customers); do 
	for P in $(echo $all_products); do
		echo ${C}-${P}-resources
	  /usr/lib/zabbix/externalscripts/trap_resources.sh $C $P
		echo ${C}-${P}-dbUsers
	  /usr/lib/zabbix/externalscripts/trap_dbUsersCheck.sh $C $P
		echo ${C}-${P}-Services
	  /usr/lib/zabbix/externalscripts/trap_services.sh $C $P
		echo ${C}-${P}-URL
	  /usr/lib/zabbix/externalscripts/trap_url_check.sh $C $P
		echo ${C}-${P}-sql check
    	  /usr/lib/zabbix/externalscripts/trap_sql_check.sh $C $P		
 		echo ${C}-${P}-file progress
          /usr/lib/zabbix/externalscripts/trap_file_progress.sh $C $P
         	echo ${C}-${P}-Version Check 
	  /usr/lib/zabbix/externalscripts/trap_agent_version_check.sh $C $P	
           	echo ${C}-${P}-Certificates Expiry Check
          /usr/lib/zabbix/externalscripts/trap_certificates.sh $C $P
                echo ${C}-${P}-Transfer Check
          /usr/lib/zabbix/externalscripts/trap_transfer_error.sh $C $P
	done

	P=eqz
		echo ${C}-${P}-integrity
	  /usr/lib/zabbix/externalscripts/trap_eqz_integrity_check.sh $C $P
		echo ${C}-${P}-newday
	  /usr/lib/zabbix/externalscripts/trap_eqz_newday_check.sh $C $P
		echo ${C}-${P}-eqis
	  /usr/lib/zabbix/externalscripts/trap_eqz_eqis_error.sh $C $P
		echo ${C}-${P}-eqps
	  /usr/lib/zabbix/externalscripts/trap_eqz_eqps_error.sh $C $P
		echo ${C}-${P}-eqws
	  /usr/lib/zabbix/externalscripts/trap_eqz_eqws_error.sh $C $P
		echo ${C}-${P}-eqxor
	  /usr/lib/zabbix/externalscripts/trap_eqz_eqxor_error.sh $C $P

	P=integrator
		echo ${C}-${P}-check
	  #/usr/lib/zabbix/externalscripts/trap_integrator_checkdata.sh $C $P checkdata
		echo ${C}-${P}-error
	  /usr/lib/zabbix/externalscripts/trap_integrator_error.sh $C $P
		echo ${C}-${P}-check intraday
	  /usr/lib/zabbix/externalscripts/trap_integrator_intraday.sh $C $P
		 echo ${C}-${P}-check checkdata 
   	  /usr/lib/zabbix/externalscripts/trap_integrator_checkdata.sh $C $P
		 echo ${C}-${P}-integrator_internal_error
          /usr/lib/zabbix/externalscripts/trap_integrator_internal_error.sh $C $P internal

	P=apsys
		echo ${C}-${P}-ApsysError
	  /usr/lib/zabbix/externalscripts/trap_apsys_error.sh $C $P
		echo ${C}-${P}-ApsysOperation
	  /usr/lib/zabbix/externalscripts/trap_apsys_operation.sh $C $P
		echo ${C}-${P}-ApsysTransaction
	  /usr/lib/zabbix/externalscripts/trap_apsys_transaction.sh $C $P
          echo ${C}-${P}-ApsysAPPIAError
          /usr/lib/zabbix/externalscripts/trap_apsys_appia_error.sh $C $P

	P=cim
		echo ${C}-${P}-cim_dbsrv
	  /usr/lib/zabbix/externalscripts/trap_cim_dbsrv_error.sh $C $P
		echo ${C}-${P}-cim_step1
	  /usr/lib/zabbix/externalscripts/trap_cim_manager_step1.sh $C $P
		echo ${C}-${P}-cim_step2
	  /usr/lib/zabbix/externalscripts/trap_cim_manager_step2.sh $C $P
		echo ${C}-${P}-cim_step3
	  /usr/lib/zabbix/externalscripts/trap_cim_manager_step3.sh $C $P

 P=pao
    echo ${C}-${P}-PAOErrors HQAM
    /usr/lib/zabbix/externalscripts/trap_pao_hqam_error.sh $C $P
	 echo ${C}-${P}-PAOErrors HQRM
    /usr/lib/zabbix/externalscripts/trap_pao_hqrm_error.sh $C $P

	P=bf
		echo ${C}-${P}-bf
	  /usr/lib/zabbix/externalscripts/trap_bf_error.sh $C $P

	P=ebk
		echo ${C}-${P}-ebk
	  /usr/lib/zabbix/externalscripts/trap_ebk_error.sh $C $P

	P=las
    echo ${C}-${P}-LASEDMSError
    /usr/lib/zabbix/externalscripts/trap_las_edms_error.sh $C $P
    echo ${C}-${P}-LASLASError
    /usr/lib/zabbix/externalscripts/trap_las_las_error.sh $C $P
    echo ${C}-${P}-LASAutoArchIdle
    /usr/lib/zabbix/externalscripts/trap_las_autoarc_idle.sh $C $P
		echo ${C}-${P}-LASAutoArchError
    /usr/lib/zabbix/externalscripts/trap_las_autoarc_error.sh $C $P

	/usr/lib/zabbix/externalscripts/trap_navigation_tree.sh $C
	/usr/lib/zabbix/externalscripts/trap_products_info.sh $C

done

/usr/lib/zabbix/externalscripts/trap_all_customers.sh all

#rm /tmp/trap.pid
