#!/bin/sh
if [ ! -f ~/warp-engine/RUNNING_PID ]
then
    echo 'not running. restarting...'
    sh ~/warp-engine/shells/start.sh
fi

