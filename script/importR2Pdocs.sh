#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+-------------------------------------------------+"
echo "| SASKIA RembrandtedDocument 2 NEPool sync script |"
echo "+-------------------------------------------------+"
echo ""
echo "What user or user_id?"
read USR
echo "What collection do you want to sync?"
read COL
echo "How many docs would you like to sync?"
read N
echo "OK. Starting script..."

java $JAVA_OPTS saskia.imports.ImportRembrandtedDocument2NEPool --from=batch --user=$USR --col=$COL --ndocs=$N
