#!/bin/sh
cd ~/warp-engine/
echo "adding crontab for monitoring..."
(crontab -l 2>/dev/null; echo "*/1 * * * * ~/warp-engine/shells/monitor.sh >> ~/warp.log ") | crontab -
exit 0



