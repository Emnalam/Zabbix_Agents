#!/bin/bash

cwd=$(dirname $0)


source ${BASH_SOURCE%/*}/global_traps.sh

source ${BASH_SOURCE%/*}/${1}_variables.sh

echo Close >> $cwd/out_close.txt
echo $1 >> $cwd/out_close.txt
echo $2 >> $cwd/out_close.txt
echo $3 >> $cwd/out_close.txt
echo $cwd >> $cwd/out_close.txt


echo $customer_servicedesk_name >> $cwd/out_close.txt

