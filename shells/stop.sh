#!/bin/sh
cd ~/warp-dist/
if [ -f ~/warp-dist/RUNNING_PID ]
then
    PID=$(tail RUNNING_PID)
    echo "stopping server $PID"
    kill -15 $PID
    # kill java to free resources
    pkill java
    rm ~/warp-dist/RUNNING_PID
fi
exit 0



