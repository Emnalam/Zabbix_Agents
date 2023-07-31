#!/bin/sh
SCRIPTPATH=$(dirname "$0")

. $SCRIPTPATH/variables.sh

usage() {
  cat <<!

    New Access monitoring agents
   
    usage: nwamon.sh [-h] [-r|-k|-c|-v] [-s] [-x settings.xml]
    
             -h|--help        Usage (this text)
             -r|--run         Launch the agents
             -k|--kill        Stop the running agents
             -c|--check       Checks an XML parameter file (Agents have to be stopped for this option)
             -v|--version     Shows implementation version and java sdk buil version
             -s|--status      Control the status of the Agents. Gives back the corresponding PID if started
             -x|--xml <file>  Agents parameters XML file
                              default : agents/dat/agentsSettings.xml

!
}

pid () {
 USR=`whoami`
 echo `ps -fU $USR | grep ".* Starter" | grep -v grep | awk {'print $2'}`
}

run() {
  if [ -z "$PID" ]; then
    echo "Starting agents...."
    $JAVA -cp "$CLASSPATH:.:bin/agents.jar" Starter $SETTINGS &
    sleep 2
    echo "Agents started with PID `pid`"
  else
    echo "Agents are already running with PID $PID"
    exit 1
  fi
}

end() {
  if [ "$PID" ]
  then
    echo "Stopping agents instance $PID"
    kill $PID
    echo done
  else
    echo "No agent running..."
  fi
}

status() {
  if [ -z "$PID" ]; then
    echo "No agent running..."
  else
    echo "Agents running with PID $PID"
    exit 1
  fi
}

check() {
  if [ -z "$PID" ]
  then
    echo "Checking config...."
    $JAVA -cp "$CLASSPATH:.:bin/agents.jar" Starter $SETTINGS checkConfigOnly
    echo "Done"
  else
    echo "Agents running with PID $PID - Please stop them before calling this option"
    echo "Please stop them before calling this option"
    usage
    exit 3
  fi
}

version() {
  echo "Agents Implementation and Jdk build versions:"
  $JAVA -cp "$CLASSPATH:.:bin/agents.jar" Starter version
  echo "Done"

}

SETTINGS=dat/agentsSettings.xml

if [ $# -eq 0 ]
then
  usage
  exit
fi

while [ "$1" != "" ]
do
  case $1 in
    -h | --help)              usage; exit 0;;
    -r | --run | --start)     A=R;;
    -k | --kill | --stop)     A=K;;
    -c | --check)             A=C;;
    -v | --version)           A=V;;
    -s | --status)            A=S;;
    -x | --xml)               shift; SETTINGS=$1;;
    *)                        usage; exit 1;;
  esac
  shift
done

cd `dirname $0`/..

if [ ! -f "$SETTINGS" ]
then
  echo "   Settings file ($SETTINGS) not found"
  usage
  exit 2
fi

PID=`pid`

if [ -f "$JAVA" ]; then
 case $A in
  R) run;       break;;
  K) end;       break;;
  S) status;    break;;
  V) version;   break;;
  C) check;   break;;
 esac
else
 echo $JAVA not found
fi



