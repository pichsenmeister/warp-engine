#!/bin/sh
cd ~/
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
echo "export PATH=$PATH:~/activator-1.3.2-minimal/activator" >> ~/.bashrc
echo "settings aliases..."
echo "alias warp-deploy='sh ~/warp-engine/shells/deploy.sh'" >> ~/.bashrc
echo "alias warp-start='sh ~/warp-engine/shells/start.sh'" >> ~/.bashrc
echo "alias warp-stop='sh ~/warp-engine/shells/stop.sh'" >> ~/.bashrc
echo "alias warp-restart='sh ~/warp-engine/shells/stop.sh && ~/warp-engine/shells/start.sh'" >> ~/.bashrc
echo "alias warp-monitor='sh ~/warp-engine/shells/install-monitor.sh'" >> ~/.bashrc
bash
exit 0
