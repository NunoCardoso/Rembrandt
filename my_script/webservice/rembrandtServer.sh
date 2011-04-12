#!/bin/sh
LOG=file:///var/www/html/Rembrandt/log4j.rembrandt.properties
JARS=/var/www/html/Rembrandt/jar
CONF=/var/www/html/Rembrandt/rembrandt.server.properties
CLASSPATH=`ls ${JARS}/*.jar | tr "\n" ":"`
LANG=pt
JAVA_OPTS='-server -Xms128m -Xmx1024m '

exec java $JAVA_OPTS -classpath ${CLASSPATH} -Dlog4j.configuration=${LOG} -Dfile.encoding=UTF-8 rembrandt.server.RembrandtServer ${CONF} &
