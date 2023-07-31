SERVER=192.168.141.164
USER=scpuser
ZABBIX_ACTIONS_PATH=/outputs/transfers/bendura/actions
LOCAL_ACTIONS_PATH=/export/home/nfs_monitoring/utils/actions
CENTRAL_ACTIONS_PATH=/monitoring/ACTIONS

CURRENT_TIME=$(date +%H%M)

while [ "$CURRENT_TIME" -lt 2359 -a "$CURRENT_TIME" -gt 0005 ]; do
	CURRENT_TIME=$(date +%H%M)
        find ${CENTRAL_ACTIONS_PATH} -mmin +10 -type f -exec rm -f {} \; >> ~/log/actions_$(date +'%Y%m%d').log 2>&1	
	sftp $USER@$SERVER <<! >> ~/log/actions_$(date +'%Y%m%d').log 2>&1 
	cd ${ZABBIX_ACTIONS_PATH} >> ~/log/actions_$(date +'%Y%m%d').log 2>&1 
	chmod -R 777 . >> ~/log/actions_$(date +'%Y%m%d').log 2>&1
	get * ${LOCAL_ACTIONS_PATH} >> ~/log/actions_$(date +'%Y%m%d').log 2>&1 
	rm * >> ~/log/actions_$(date +'%Y%m%d').log 2>&1
	exit
!
	chmod -R 777 ${LOCAL_ACTIONS_PATH} >> ~/log/actions_$(date +'%Y%m%d').log 2>&1 
	cp -f ${LOCAL_ACTIONS_PATH}/* ${CENTRAL_ACTIONS_PATH}
	rm -f ${LOCAL_ACTIONS_PATH}/*
	sleep 10
done 
