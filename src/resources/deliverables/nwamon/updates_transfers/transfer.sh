# THIS IS THE SCRIPT USED AT BENDURA TO SEND AGENTS FILES TO ZABBIX
# IT CAN BE ADAPTED TO YOUR NEEDS.

#ZABBIX SERVER
SERVER=192.168.141.164
#ZABBIX USER
USER=scpuser

CURRENT_TIME=$(date +%H%M)

while [ "$CURRENT_TIME" -lt 2359 -a "$CURRENT_TIME" -gt 0005 ]; do
	CURRENT_TIME=$(date +%H%M) 
	
  #SEND PRODUCTION FILES TO ZABBIX
  for file in /monitoring/PROD/*.xml; do
		sftp $USER@$SERVER <<! >> ~/log/transfer_prod_$(date +'%Y%m%d').log 2>&1 
		cd /outputs/bendura_prod
		put $file
		chmod 777 /outputs/bendura_prod/$(basename $file)
		exit
!

    #REMOVE FILES FROM CENTRAL REPOSITORY
		rm -f $file
	done

  #SEND UAT FILES TO ZABBIX
	for file in /monitoring/UAT/*.xml; do
		sftp $USER@$SERVER <<! >>  ~/log/transfer_uat_$(date +'%Y%m%d').log 2>&1 
        	cd /outputs/bendura_uat
        	put $file
        	chmod 777 /outputs/bendura_uat/$(basename $file)
        	exit
!
    #REMOVE FILES FROM CENTRAL REPOSITORY
    rm -f $file
	done
sleep 10
done
