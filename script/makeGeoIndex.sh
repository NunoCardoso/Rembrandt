#!/bin/bash

# if JAVA_OPTS is unset, use it
if [ -n "${JAVA_OPTS-x}" ]; then 
    JAVA_OPTS='-server -Dfile.encoding=UTF-8'
fi

echo "+------------------------------------------+"
echo "| SASKIA Generate Geo Index for Collection |"
echo "+------------------------------------------+"
echo ""
echo "What collection do you want to sync?"
read COL
echo "What is the index directory? (leave blank for default):"
read INDEXDIR
echo "OK. Starting script..."

if [ "${INDEXDIR}" != "" ]; then
    INDEXDIR="--indexdir=${INDEXDIR}"
fi

java $JAVA_OPTS saskia.index.GenerateGeoIndexForCollection --col=$COL $INDEXDIR