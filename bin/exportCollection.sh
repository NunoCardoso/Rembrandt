#!/bin/bash

JAVA_OPTS='-server'

echo "+---------------------------------------------------------+"
echo "| SASKIA ExportRembrandtedDocsToDir - collection exporter |"
echo "+---------------------------------------------------------+"
echo ""

java $JAVA_OPTS saskia.exports.ExportRembrandtedDocsToDir 
