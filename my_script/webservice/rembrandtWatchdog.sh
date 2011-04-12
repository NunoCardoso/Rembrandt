#!/bin/bash
DATE=`date "+%m%d%y [%k:%M]"`
LOGDATE=`date "+%m-%d-%y [%k:%M:%S]"`
mail=/bin/mail 
sysadmin=ncardoso@xldb.di.fc.ul.pt
rembrandtfile=/tmp/rembrandtWatchdog.txt
logfile=/var/log/rembrandt/rembrandtWatchdog.log
pidfile=/var/run/rembrandt.pid

#echo "$LOGDATE - rembrandt Watchdog starting up " >> $logfile

function stoprembrandt
{
/sbin/service rembrandt stop
}
function reportrestart
{
echo "Rembrandt Service Down, attempting to restart on $DATE" >> $rembrandtfile
cat $rembrandtfile | $mail -s 'Rembrandt Restart' $sysadmin
rm $rembrandtfile
}
function startrembrandt
{
/sbin/service rembrandt start
}

function checkpid
{
DATE=`date "+%m%d%y [%k:%M]"`
LOGDATE=`date "+%m-%d-%y [%k:%M:%S]"`

#get the pid, if it exists
if [ ! -f $pidfile ];then
echo "$LOGDATE - Dead PID" >> $logfile
startrembrandt
reportrestart
fi
thispid=`cat $pidfile`

if [ ! -d "/proc/$thispid" ];then
echo "$LOGDATE - there is a pid file, but no /proc/$thispid. Restarting" >> $logfile
startrembrandt
reportrestart
else
echo "$LOGDATE - Normal Rembrandt Running" >> $logfile
fi
}



checkpid


