#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+-------------------------------------------------------+"
echo "| SASKIA ImportPlainText 2 SourceDocument import script |"
echo "+-------------------------------------------------------+"
echo ""

java $JAVA_OPTS saskia.imports.ImportPlainText_2_SourceDocument 
