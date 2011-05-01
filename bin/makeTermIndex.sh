#!/bin/bash

# if JAVA_OPTS is unset, use it
 if [ -n "${JAVA_OPTS-x}" ]; then 
    JAVA_OPTS='-server -Dfile.encoding=UTF-8'
fi

echo "+-------------------------------------------+"
echo "| SASKIA Generate Term Index for Collection |"
echo "+-------------------------------------------+"
echo ""
echo "What database (main/test)? (blank means main)"
read DB
echo "What collection do you want to sync?"
read COL
echo "Do stem (true) or not (false)?"
read STEM
echo "OK. Starting script..."

if [ "${DB}" != "" ]; then
    DB="main"
fi

java -server -Dfile.encoding=UTF-8 saskia.index.GenerateTermIndexForCollection --db=$DB --col=$COL --stem=$STEM
