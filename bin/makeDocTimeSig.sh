#!/bin/bash

# if JAVA_OPTS is unset, use it
if [ -n "${JAVA_OPTS-x}" ]; then
   JAVA_OPTS='-server -Dfile.encoding=UTF-8'
fi

echo "+--------------------------------------------+"
echo "| SASKIA Generate Temporal Signatures script |"
echo "+--------------------------------------------+"
echo ""
echo "What Database (main/test)? (blank menas main)"
read DB
echo "What collection do you want to sync?"
read COL
echo "How many docs would you like to sync?"
read N
echo "Get Geographic NEs/entities from the RembrandtDoc text (rdoc) or from the NEs/entities synced to the pool (pool)?"
read SYNC
echo "OK. Starting script..."
if [ "${DB}" != "" ]; then
    DB="main"
fi

java $JAVA_OPTS saskia.imports.GenerateTimeSignatures --db=$DB --col=$COL --ndocs=$N --sync=$SYNC
