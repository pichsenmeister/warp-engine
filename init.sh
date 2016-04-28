#!/bin/sh
engine=$(pwd)
echo "downloading resources..."
apt-get -y install unzip
apt-get -y install openjdk-7-jdk
apt-get -y install redis-server
apt-get -y install wget
echo "downloading typesafe activator..."
wget http://downloads.typesafe.com/typesafe-activator/1.3.2/typesafe-activator-1.3.2-minimal.zip
echo "unzip typesafe activator..."
unzip typesafe-activator-1.3.2-minimal.zip
rm typesafe-activator-1.3.2-minimal.zip
echo "settings PATH variables..."
echo "export PATH=$PATH:~/activator-1.3.2-minimal/" >> ~/.bashrc
echo "settings aliases..."
echo "alias warp-deploy='sh $engine/shells/deploy.sh'" >> ~/.bashrc
echo "alias warp-start='sh $engine/shells/start.sh'" >> ~/.bashrc
echo "alias warp-stop='sh $engine/shells/stop.sh'" >> ~/.bashrc
echo "alias warp-restart='sh $engine/shells/stop.sh && $engine/shells/start.sh'" >> ~/.bashrc
echo "alias warp-monitor='sh $engine/shells/install-monitor.sh'" >> ~/.bashrc
source ~/.bashrc
activator dist
unzip $engine/target/universal/warp-engine-1.0-SNAPSHOT.zip
mv $engine/target/universal/warp-engine-1.0-SNAPSHOT ../
exit 0
