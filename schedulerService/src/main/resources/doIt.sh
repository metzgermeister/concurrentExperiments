#!/bin/bash 
function experiment {


for (( c=1; c<=5; c++ ))
do
   
    echo " experiment number $c for $1 $2 $3 " 

    notebookBefore="$(grep 'acquired worker url' ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep -c 222)"
    clusterBefore="$(grep 'acquired worker url' ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep -c 127)"
    
    curl "http://node6.grid.isofts.kiev.ua:8080/schedulerService/schedulerServices/twoClientsExperiment/minMax/$1?firstClientBlockSize=$2&secondClientBlockSize=$3"

    notebookAfter="$(grep 'acquired worker url' ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep -c 222)"
    clusterAfter="$(grep 'acquired worker url' ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep -c 127)"

    totalTasks=$((notebookAfter - notebookBefore + clusterAfter - clusterBefore))
    
    if [ ${totalTasks} -eq 0 ]
        then
            echo " total tasks = 0 something went wrong"
        else
         echo " total tasks ${totalTasks}"
          notebookPercent=$(((notebookAfter - notebookBefore)*100/totalTasks))
          clusterPercent=$(((clusterAfter - clusterBefore)*100/totalTasks))
          echo "notebook ${notebookPercent} %  cluster ${clusterPercent} %"
    fi
    
    grep "total calculation time is" ~/java/apache-tomcat-8.0.18/logs/scheduler-service.log | grep "first client blockSize=$2 second client blockSize=$3"
   
    sleep 2
done

}

experiment 1200 50 50
experiment 1200 50 100
experiment 1200 50 200
experiment 1200 50 400
experiment 1200 50 600
experiment 1200 50 1200

experiment 1200 100 100
experiment 1200 100 200
experiment 1200 100 400
experiment 1200 100 600
experiment 1200 100 1200

experiment 1200 200 200
experiment 1200 200 400
experiment 1200 200 600
experiment 1200 200 1200

experiment 1200 400 400
experiment 1200 400 600
experiment 1200 400 1200

experiment 1200 600 600
experiment 1200 600 1200

experiment 1200 1200 1200
