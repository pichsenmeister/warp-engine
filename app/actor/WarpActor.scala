package actor

import akka.actor._
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.Play.current


object WarpActor {

    def props(out: ActorRef, channel: String, token: String) = {
        Props(new WarpActor(out, channel, token))
    }
}

class WarpActor(out: ActorRef, channel: String, token: String) extends Actor {

    val child = Akka.system.actorOf(RequestActor.props(out, channel, token), token)
    Logger.debug("created listener: "+child)

    def receive = {
        case msg: JsValue =>
            Logger.debug("received in WarpActor: "+msg)
            val actor = Akka.system.actorSelection("user/*/"+channel)
            actor ! msg
    }

    override def postStop() = {
        child ! PoisonPill
    }
}

object RequestActor {

    def props(out: ActorRef, channel: String, token: String) = {
        Props(new RequestActor(out, channel, token))
    }
}

class RequestActor(out: ActorRef, channel: String, token: String) extends Actor {

    val child = context.actorOf(ChannelActor.props(out), channel)
    Logger.debug("created listener: "+child)

    def receive = {
        case msg: JsValue =>
            Logger.debug("received in RequestActor: "+msg)
            val actor = Akka.system.actorSelection("user/channels/*/"+channel)
            out ! msg
    }

    override def postStop() = {
        child ! PoisonPill
    }
}

object ChannelActor {

    def props(out: ActorRef) = {
        Props(new ChannelActor(out))
    }
}

class ChannelActor(out: ActorRef) extends Actor {

    def receive = {
        case msg: JsValue =>
            Logger.debug("received in ChannelActor: "+msg)
            val actor = Akka.system.actorSelection("user/channels/*/")
            out ! msg
    }
}