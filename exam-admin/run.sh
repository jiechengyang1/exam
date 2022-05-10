#!/bin/sh

if [ -z $JAVA_OPTS ];then
    JAVA_OPTS="-Xms256m -Xmx512m"
fi

if [ -z $JAR_PATH ];then
    JAR_PATH="/opt/exam"
fi

if [ x$LOG != "xfalse" ];then
    mkdir -p logs
    LOGGING_OPT="--logging.path=./logs"
fi

echo $JAVA_OPTS -Dlogging.path=./logs -jar ${JAR_PATH}/*.jar

java $JAVA_OPTS -Dlogging.path=./logs -jar ${JAR_PATH}/*.jar
