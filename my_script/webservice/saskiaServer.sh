#!/bin/sh
LOG=file:///var/www/html/Rembrandt/log4j.saskia.properties
JARS=/var/www/html/Rembrandt/jar
CLASSPATH=`ls ${JARS}/*.jar | tr "\n" ":"`
CONF=/var/www/html/Rembrandt/rembrandt.server.properties
LANG=pt
JAVA_OPTS='-server -Xms128m -Xmx1024m -Dfile.encoding=UTF-8 ' 
export WEBSTORE_CONFIG_FILE=/usr/local/webstore/conf/webstore-conf.xml
export WEBSTORE_DATA=/collections/webstore
export WEBSTORE_HOME=/usr/local/webstore

java $JAVA_OPTS -classpath $CLASSPATH  -Dlog4j.configuration=${LOG} saskia.server.SaskiaServer ${CONF} &
