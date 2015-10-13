package actor

import akka.actor._
import messages.Subscribe
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.{Json, JsValue}
import akka.actor.SupervisorStrategy.Stop

object WarpActor {
    def props(token: String)(out: ActorRef) =
        Props(new WarpActor(out, token))
}

class WarpActor(out: ActorRef, token: String) extends Actor {

    val child = context.watch(Akka.system.actorOf(RequestActor.props(out), token))
    val hook: ActorRef = context.actorOf(HookActor.props)

//    val child = Akka.system.actorOf(RequestActor.props(out), token)
    Logger.debug("created request listener: "+child)

    def receive = {
        case subscribe: JsValue if (subscribe \ "subscribe").asOpt[String].isDefined =>
            val channel: String = (subscribe \ "subscribe").asOpt[String].getOrElse("default")
            val sub: Subscribe = Subscribe(token, channel, (subscribe \ "data").asOpt[JsValue])
            child ! sub

            val actor = Akka.system.actorSelection("user/*/"+channel)
            actor ! sub

            sendToHook(channel, Json.obj("sub" -> Json.toJson(sub)))
        case msg: JsValue =>
            val channel: String = (msg \ "channel").asOpt[String].getOrElse("default")

            val actor = Akka.system.actorSelection("user/*/"+channel)
            actor ! msg

            sendToHook(channel, msg)
    }

    // Stop the child if it gets an exception
    override val supervisorStrategy = OneForOneStrategy() {
        case _ =>
            Logger.debug("supervisor strategy: "+self)
            Stop
    }

    override def postStop() = {
        Logger.debug("kill WarpActor: "+self)
        child ! PoisonPill
        hook ! PoisonPill
    }

    private def sendToHook(channel: String, msg: JsValue): Unit = {
        hook ! (channel, msg)
    }
}