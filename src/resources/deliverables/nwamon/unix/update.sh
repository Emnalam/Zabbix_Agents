#!/bin/sh
SCRIPTPATH=$(dirname "$0")

cd $SCRIPTPATH

./install.sh -update >> $SCRIPTPATH/update_log.txt
