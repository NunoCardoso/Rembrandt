#!/bin/sh
NEEDLE="RembrandtServer"
PID=`ps -eo pid,args | grep ${NEEDLE} | grep -v grep | cut -c1-6`
echo "PID: $PID"
kill -9 $PID