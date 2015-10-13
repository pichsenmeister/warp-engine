package actor

import akka.actor.{PoisonPill, Props, Actor, ActorRef}
import messages.Subscribe
import play.api.Logger
import play.api.libs.json.Json

object RequestActor {

    def props(out: ActorRef) = Props(new RequestActor(out: ActorRef))
}

class RequestActor(out: ActorRef) extends Actor {

    def receive = {
        case subscribe: Subscribe =>
            val child = context.actorOf(ChannelActor.props(out), subscribe.channel)
            child ! subscribe
            Logger.debug("created channel listener: " + child)
    }

    override def postStop = {
        Logger.debug("kill RequestActor: "+self)
        context.children.foreach(_ ! PoisonPill)
    }

}

