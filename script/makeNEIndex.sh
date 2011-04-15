#!/bin/bash

# if JAVA_OPTS is unset, use it
if [ -n "${JAVA_OPTS-x}" ]; then 
    JAVA_OPTS='-server -Dfile.encoding=UTF-8'
fi

echo "+-----------------------------------------+"
echo "| SASKIA Generate NE Index for Collection |"
echo "+-----------------------------------------+"
echo ""
echo "What collection do you want to sync?"
read COL
echo "Get NEs from the RembrandtDoc text (rdoc) or from the NEs synced to the pool (pool)?"
read SYNC
echo "OK. Starting script..."

java $JAVA_OPTS saskia.index.GenerateNEIndexForCollection --col=$COL --sync=$SYNC
