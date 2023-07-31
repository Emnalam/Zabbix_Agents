#!/bin/bash
source ${BASH_SOURCE%/*}/global_traps.sh

status="ok"
files=`find /tmp -name "*.status"  2>/dev/null`
gstatus="ok"
for f in $files; do
  status=`cat $f`
  if [ "$status" = "warning" ]; then
    gstatus="warning";
  else if [ "$status" = "problem" ]; then
         gstatus="problem";
         break;
       fi
  fi
done
if [ "$gstatus" = "ok" ]; then
  printf "<img src='https://66.media.tumblr.com/c4ffd71c7cdb224bdf913f21a2225628/tumblr_oue6l82MXZ1su7gxzo1_400.gifv' width='80' height='80' />"
fi
if [ "$gstatus" = "warning" ]; then
  printf "<img src='https://www.iconsdb.com/icons/preview/orange/warning-28-xxl.png' width='80' height='80' />"
fi
if [ "$gstatus" = "problem" ]; then
  printf "<img src='https://www.iconsdb.com/icons/preview/red/warning-xxl.png' width='80' height='80' />"
fi
