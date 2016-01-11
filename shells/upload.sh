#!/bin/bash
cd ~/git/warp-engine
activator dist
scp ~/git/warp-engine/target/universal/warp-engine-1.0-SNAPSHOT.zip root@46.101.201.78:~/warp-dist.zip
exit 0
