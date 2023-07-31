#!/bin/sh

usage() {
  cat <<!
    install.sh - Install the New Access Monitoring Agents
    Usage: install.sh [-i] <owner>
!
}

pid () {
 USR=`whoami`
 echo `ps -fU $USR | grep ".* Starter" | grep -v grep | awk {'print $2'}`
}

binaries() {
  echo "Starting installation of binaries"
  if [ -d "$DIRECTORY" ]
  then
    echo "Upgrading binaries..."
    cp kit/nwamon.sh kit/encryptValue.sh ${DIRECTORY}/bin
    cp ../agents.jar ${DIRECTORY}/bin
    if [ ! -f "${DIRECTORY}/bin/transfer.sh" ]; then
      cp kit/nwamon.sh ${DIRECTORY}/bin
    fi
    if [ ! -f "${DIRECTORY}/bin/check_updates.sh" ]; then
      cp kit/check_updates.sh ${DIRECTORY}/bin
    fi
    if [ ! -f "${DIRECTORY}/bin/variables.sh" ]; then
      cp kit/variables.sh ${DIRECTORY}/bin
    fi
  else 
    echo "Creating directory $DIRECTORY ..."
    mkdir -p $DIRECTORY 
    mkdir -p ${DIRECTORY}/bin 

    echo "Copying binaries..."
    cp kit/* ${DIRECTORY}/bin
	
    echo "Copying Agents..."
    cp ../agents.jar ${DIRECTORY}/bin

    mkdir -p $DIRECTORY/dat $DIRECTORY/log $DIRECTORY/output
    chown $OWNER:$OWNER $DIRECTORY $DIRECTORY/dat $DIRECTORY/log $DIRECTORY/output
  fi

  if [ ! -d "$DIRECTORY/updates" ]; then
    mkdir -p $DIRECTORY/updates 
    chown $OWNER:$OWNER $DIRECTORY/updates
  fi

  echo Done! 
  chmod -R a+rx ${DIRECTORY}
}

if [ -z "$1" ]
then 
  usage
  exit 1
fi

if [ "$1" = "-update" ]
then
  #wait 2 seconds for agents to shutdown by java code....
  sleep 2
fi

PID=`pid`
if [ "$PID" != "" ]; then
  echo Nwamon is already running with PID $PID. Please stop the process first
  exit 1
fi

if [ "$1" = "-i" ] || [ "$1" = "-update" ]
then
  if [ -z "$2" ]
  then
	OWNER=$USER
  else
	OWNER=$2
  fi
  
  HOMEDIR=`eval echo ~$OWNER`

  if [ -d $HOMEDIR ];
	then	
  	DIRECTORY=$HOMEDIR/agents
		binaries
	else
	  echo Owner $OWNER does not exist
	fi
fi

if [ "$1" = "-update" ]
then
  cd ${DIRECTORY}/bin
  ./nwamon.sh -r
fi
