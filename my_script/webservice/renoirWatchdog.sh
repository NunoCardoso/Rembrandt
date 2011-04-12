#!/bin/bash
DATE=`date "+%m%d%y [%k:%M]"`
LOGDATE=`date "+%m-%d-%y [%k:%M:%S]"`
mail=/bin/mail 
sysadmin=ncardoso@xldb.di.fc.ul.pt
renoirfile=/tmp/renoirWatchdog.txt
logfile=/var/log/rembrandt/renoirWatchdog.log
pidfile=/var/run/renoir.pid

#echo "$LOGDATE - rembrandt Watchdog starting up " >> $logfile

function stoprembrandt
{
/sbin/service renoir stop
}
function reportrestart
{
echo "Renoir Service Down, attempting to restart on $DATE" >> $renoirfile
cat $renoirfile | $mail -s 'Renoir Restart' $sysadmin
rm $renoirfile
}
function startrembrandt
{
/sbin/service renoir start
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
echo "$LOGDATE - Normal Renoir Running" >> $logfile
fi
}



checkpid


