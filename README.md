# warp-engine


**W** ieldy **A** utomatic **R** eactive **P** ush - Engine

## setup

follow this instruction for ubuntu 14.04 x64. it should also work on other linux systems.

update apt-repository and install git
```
sudo apt-get update && sudo apt-get install git
```

clone git repository via https
```
git clone https://github.com/3x14159265/warp-engine.git
```

run init script (involves pressing "y" once at a time, since it's installing dependencies via apt-get)
```
sh ~/warp-engine/init.sh
```

start warp-engine (this will take a few minutes. when everything works fine, the server will start. you can leave server logging with ```ctrl+d```)
```
warp-start
```

install the monitor cronjob to monitor and restart the warp-engine, if it's down
```
warp-monitor
```


### some other helpful commands:

* ```warp-stop``` stops the warp-engine
* ```warp-start``` starts the warp-engine
* ```warp-restart``` restarts the warp-engine
* ```warp-deploy``` fetches master from github and starts warp-engine
