package actor

import akka.actor._
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue

case class Subscribe(token: String, channel: String)
case class ClientMessage(token: String, json: JsValue)

object WarpActor {
    def props(token: String)(out: ActorRef) =
        Props(new WarpActor(out, token))
}

class WarpActor(out: ActorRef, token: String) extends Actor {

    val child = Akka.system.actorOf(RequestActor.props(out), token)
    Logger.debug("created request listener: "+child)

    def receive = {
        case subscribe: JsValue if (subscribe \ "subscribe").asOpt[String].isDefined =>
            val channel: String = (subscribe \ "subscribe").asOpt[String].getOrElse("default")
            Logger.debug("subscribe: "+channel)
            child ! Subscribe(token, channel)
        case msg: JsValue =>
            val channel: String = (msg \ "channel").asOpt[String].getOrElse("default")
            val actor = Akka.system.actorSelection("user/*/"+channel)
            Logger.debug("message: "+channel)
            actor ! msg
    }

    override def postStop() = {
        Logger.debug("kill WarpActor: "+self)
        child ! PoisonPill
    }
}

object RequestActor {

    def props(out: ActorRef) = Props(new RequestActor(out: ActorRef))
}

class RequestActor(out: ActorRef) extends Actor {

    def receive = {
        case subscribe: Subscribe =>
            val child = context.actorOf(ChannelActor.props(out), subscribe.channel)
            Logger.debug("created channel listener: " + child)
    }

    override def postStop = {
        Logger.debug("kill RequestActor: "+self)
    }

}

object ChannelActor {

    def props(out: ActorRef) = Props(new ChannelActor(out: ActorRef))
}

class ChannelActor(out: ActorRef) extends Actor {

    def receive = {
        case msg: JsValue =>
            Logger.debug("received in ChannelActor: "+msg)
            out ! msg
    }

    override def postStop = {
        Logger.debug("kill ChannelActor: "+self)
    }

}