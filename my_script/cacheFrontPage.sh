#!/bin/bash 

JAVA_OPTS='-server -Xms256m -Xmx1024m -Dfile.encoding=UTF-8'

echo "+----------------------------------------------------+"
echo "| Cache refreshener for SASKIA collection statistics |"
echo "+----------------------------------------------------+"
echo ""
echo "Which collection?"
read COL

echo "OK, syncing for PT..."
java $JAVA_OPTS rembrandtpool.stats.RembrandtPoolStats -refreshMain $COL pt
echo "OK, syncing for EN..."
java -server rembrandtpool.stats.RembrandtPoolStats -refreshMain $COL en
echo "Done."