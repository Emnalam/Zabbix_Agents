#!/bin/sh
#
# New Access Monitoring Agents - Unix installation Script
#
# >>> Take care that is file has to be customized for each installation
#
# History:
# 2019.04.09 - RSA - Creation
# 2019.04.11 - FRD - SCP

for i in "$@"
do
case $i in
    -s=*|--server=*)
    SERVER="${i#*=}"
    shift # past argument=value
    ;;
    -l=*|--login=*)
    LOGIN="${i#*=}"
    shift # past argument=value
     ;;
    -p=*|--password=*)
    PASSWORD="${i#*=}"
    shift # past argument=value
    ;;
    -pt=*|--port=*)
    PORT="${i#*=}"
    shift # past argument=value
     ;;
    -f=*|--file=*)
    FILE="${i#*=}"
    shift # past argument=value
    ;;
    -rf=*|--remotefile=*)
    REMOTEFILE="${i#*=}"
    shift # past argument=value
    ;;
    --default)
    DEFAULT=YES
    shift # past argument with no value
    ;;
    *)
          # unknown option
    ;;
esac 
done

scp -p -P $PORT ${LOGIN}@${SERVER}:/monitoring/UPDATES/* ~/agents/updates/
