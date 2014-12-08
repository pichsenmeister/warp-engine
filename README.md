# warp-engine

**check out [warp-engine.org](http://www.warp-engine.org) for further informations!**


warp-engine is an open-source, realtime push engine, written in [scala](http://scala-lang.org/), build on [play](https://playframework.com/) and [akka](http://akka.io/). It's fully reactive and easy to setup (~10-15 minutes).
(by [@3x14159265](https://www.twitter.com/3x14159265))

the instructions only covers setup on an ubuntu 14.04 x64 server. i recommend using [digitalocean](https://www.digitalocean.com/?refcode=06560b6c098a) (min. $20 droplet is required), since it's easy to setup and quite cheap. 
you can do me a favour and use me referrel link ([this link](https://www.digitalocean.com/?refcode=06560b6c098a)), if you haven't signed up for digitalocean yet. you'll get a $10 if you signup via my link:

[https://www.digitalocean.com/?refcode=06560b6c098a](https://www.digitalocean.com/?refcode=06560b6c098a)

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

start warp-engine (this will take a few minutes. better grab a coffee :coffee:. when everything works fine, the server will start. you can escape server logging with ```ctrl+d```)
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


## license

Source code licensed under [GPL v3.0](http://www.gnu.org/copyleft/gpl.html), documentation under [CC BY 3.0](http://creativecommons.org/licenses/by/3.0/).
