#/bin/sh

export CLASSPATH=`ls lib/*.jar | tr "\n" ":"`

exec java -server -Xmx512m -Xms256m -cp $CLASSPATH -Dfile.encoding=UTF-8 renoir.bin.RenoirShell --col=$1