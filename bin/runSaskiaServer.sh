#/bin/sh
exec java -server -Xmx512m -Xms256m -Dfile.encoding=UTF-8 saskia.server.SaskiaServer &
NEEDLE="SaskiaServer"
PID=`ps -eo pid,args | grep ${NEEDLE} | grep -v grep | cut -c1-6`
echo "PID: $PID"