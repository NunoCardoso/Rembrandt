#!/bin/bash

JAVA_OPTS='-server -Dfile.encoding=UTF-8'

echo "+----------------------------------------------------+"
echo "| SASKIA ImportPagico 2 SourceDocument import script |"
echo "+----------------------------------------------------+"
echo ""

java $JAVA_OPTS saskia.imports.ImportPagico_2_SourceDocument --dir=/Users/nunocardoso/Pagico/coleccao/pt
