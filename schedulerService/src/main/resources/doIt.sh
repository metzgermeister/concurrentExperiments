#!/bin/bash 
function experiment {
echo " experiment for $1 $2 $3" 

curl "http://node6.grid.isofts.kiev.ua:8080/schedulerService/schedulerServices/twoClientsExperiment/$1?firstClientBlockSize=$2&secondClientBlockSize=$3"


echo "tasks processed by notebook "
grep "acquired worker url" ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep -c 222


echo "tasks processed by cluster "
grep "acquired worker url" ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep -c 127

grep "total calculation time is" ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep "first client blockSize=$2 second client blockSize=$3"
}

experiment 1200 50 50


