#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+-------------------------------------------------+"
echo "| SASKIA RembrandtedDocument 2 NEPool sync script |"
echo "+-------------------------------------------------+"
echo ""

java $JAVA_OPTS saskia.imports.ImportRembrandtedDocument_2_NEPool 
