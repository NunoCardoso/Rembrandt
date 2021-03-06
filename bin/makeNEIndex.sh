#!/bin/bash

# if JAVA_OPTS is unset, use it
if [ -n "${JAVA_OPTS-x}" ]; then 
    JAVA_OPTS='-server -Dfile.encoding=UTF-8'
fi

echo "+-----------------------------------------+"
echo "| SASKIA Generate NE Index for Collection |"
echo "+-----------------------------------------+"
echo ""
echo "Which database (main/test)? (blank means main)"
read DB
echo "What collection do you want to sync?"
read COL
echo "Get NEs from the RembrandtDoc text (rdoc) or from the NEs synced to the pool (pool)?"
read SYNC
echo "OK. Starting script..."

if [ "${DB}" != "" ]; then
    DB="main"
fi

java $JAVA_OPTS saskia.index.GenerateNEIndexForCollection --db=$DB --col=$COL --sync=$SYNC
