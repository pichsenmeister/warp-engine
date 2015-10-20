package actor

import akka.actor._
import messages.{ClientMessage, Subscribe}
import org.joda.time.DateTime
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
    Logger.debug("created warp actor: "+self)
    Logger.debug("created request listener: "+child)

    def receive = {
        case subscribe: JsValue if (subscribe \ "subscribe").asOpt[String].isDefined =>
            val channel: String = (subscribe \ "subscribe").asOpt[String].getOrElse("default")
            val sub: Subscribe = Subscribe(token, channel, DateTime.now().getMillis(), (subscribe \ "data").asOpt[JsValue])
            child ! sub

            notify(channel, sub)
            hook(channel, sub)

        case msg: JsValue =>
            val channel: String = (msg \ "channel").asOpt[String].getOrElse("default")
            val message: ClientMessage = ClientMessage(token, channel, (msg \ "msg"), DateTime.now().getMillis())

            notify(channel, message)
            hook(channel, message)

    }

    // Stop the child if it gets an exception
//    override val supervisorStrategy = OneForOneStrategy() {
//        case _ =>
//            Logger.debug("supervisor strategy: "+self)
//            Stop
//    }

    override def postStop() = {
        Logger.debug("kill WarpActor: "+self)
        child ! PoisonPill
    }

    private def notify(channel: String, msg: Any): Unit = {
        val actor = Akka.system.actorSelection("user/*/"+channel)
        actor ! msg
    }

    private def hook(channel: String, msg: Any): Unit = {
        val hook = Akka.system.actorSelection("user/hook")
        hook ! (channel, msg)
    }

}