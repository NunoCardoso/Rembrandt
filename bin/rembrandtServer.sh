#/bin/sh
exec java -server -Xmx512m -Xms256m -Dfile.encoding=UTF-8 rembrandt.server.RembrandtServer  &
NEEDLE="RembrandtServer"
PID=`ps -eo pid,args | grep ${NEEDLE} | grep -v grep | cut -c1-6`
echo "PID: $PID"