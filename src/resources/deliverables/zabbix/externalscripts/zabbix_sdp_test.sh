cwd=$(dirname $0)
sdpid=`java -jar $cwd/SdpClient.jar -url "http://ch-mis-wks-rsa:9999" -createRequest -subject "$1" -description "$2" -site "$3" -product "$4" -priority "3 - High" -group "CS TECH"` 
#echo $sdpid > $cwd/$2
#chmod 777 $cwd/$2
