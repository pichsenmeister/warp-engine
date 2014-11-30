#!/bin/sh
cd ~/warp-engine/
echo "fetching master..."
git pull
PID=$(tail RUNNING_PID)
echo "stopping server $PID"
kill -15 $PID
# kill java to free resources
pkill java
echo "starting server..."
sh ~/warp-engine/shells/start.sh
exit 0


