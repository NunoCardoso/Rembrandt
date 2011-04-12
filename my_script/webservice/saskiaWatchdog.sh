#!/bin/bash
DATE=`date "+%m%d%y [%k:%M]"`
LOGDATE=`date "+%m-%d-%y [%k:%M:%S]"`
mail=/bin/mail 
sysadmin=ncardoso@xldb.di.fc.ul.pt
file=/tmp/saskiaWatchdog.txt
logfile=/var/log/rembrandt/saskiaWatchdog.log
pidfile=/var/run/saskia.pid

#echo "$LOGDATE - rembrandt Watchdog starting up " >> $logfile

function stoprembrandt
{
/sbin/service saskia stop
}
function reportrestart
{
echo "SASKIA Service Down, attempting to restart on $DATE" >> $file
cat $file | $mail -s 'SASKIA Restart' $sysadmin
rm $file
}
function startrembrandt
{
/sbin/service saskia start
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
echo "$LOGDATE - Normal SASKIA Running" >> $logfile
fi
}



checkpid


