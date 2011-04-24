#!/bin/bash

# if JAVA_OPTS is unset, use it
if [ -n "${JAVA_OPTS-x}" ]; then 
    JAVA_OPTS='-server -Xms256m -Xmx1024m -Dfile.encoding=UTF-8'
fi

echo "+-------------------------------------------+"
echo "| SASKIA Generate Time Index for Collection |"
echo "+-------------------------------------------+"
echo ""
echo "What database (main/test)? (blank means main)"
read DB 
echo "What collection do you want to sync?"
read COL
echo "What is the index directory? (leave blank for default):"
read INDEXDIR
echo "OK. Starting script..."
if [ "${DB}" != "" ]; then
    DB="main"
fi

if [ "$INDEXDIR" != "" ]; then
    INDEXDIR="--indexdir=${INDEXDIR}"
fi

java $JAVA_OPTS saskia.index.GenerateTimeIndexForCollection --db=$DB --col=$COL $INDEXDIR