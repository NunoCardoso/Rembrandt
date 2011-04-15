#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+-----------------------------------------------------------+"
echo "| SASKIA SourceDocument 2 RembrandtedDocument import script |"
echo "+-----------------------------------------------------------+"
echo ""
echo "What user or user_id?"
read USR
echo "What collection do you want to perform the import?"
read COL
echo "How many docs would you like to import?"
read N
echo "OK. Starting script..."

java $JAVA_OPTS saskia.imports.ImportSourceDocument2RembrandtedDocument --from=batch --col=$COL --user=$USR --ndocs=$N
