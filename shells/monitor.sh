#!/bin/sh
if [ ! -f ~/warp-engine/RUNNING_PID ]
then
    echo 'not running. restarting...'
    # kill java to free resources
    pkill java
    sh ~/warp-engine/shells/start.sh
fi
exit 0

