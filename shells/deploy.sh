#!/bin/sh
cd /root/shells
echo "fetching shells..."
git pull
cd ~/warp-dist
PID=$(tail ./RUNNING_PID)
echo "stopping server $PID"
kill -15 $PID
pkill java
sleep 2
echo "moving dist directory..."
cd ~/
now=$(date +'%Y%m%d-%H%M')
mv warp-dist warp-dist-$now
mv start.log start-$now.log
echo "unzipping new version..."
unzip warp-dist.zip
mv warp-engine-1.0-SNAPSHOT warp-dist
rm warp-dist.zip
echo "starting server..."
~/warp-dist/bin/warp-engine -Dconfig.file=/root/warp-dist/conf/application.conf >> start.log &
exit 0


