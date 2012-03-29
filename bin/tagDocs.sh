#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+--------------------------------------+"
echo "| SASKIA RembrandtADocument tag script |"
echo "+--------------------------------------+"
echo ""

java $JAVA_OPTS saskia.imports.RembrandtADocument 
