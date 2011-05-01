#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+-----------------------------------------------------------+"
echo "| SASKIA SourceDocument 2 RembrandtedDocument import script |"
echo "+-----------------------------------------------------------+"
echo ""

java $JAVA_OPTS saskia.imports.ImportSourceDocument_2_RembrandtedDocument 
