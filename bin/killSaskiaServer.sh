#!/bin/sh
NEEDLE="SaskiaServer"
PID=`ps -eo pid,args | grep ${NEEDLE} | grep -v grep | cut -c1-6`
echo "PID: $PID"
kill -9 $PID