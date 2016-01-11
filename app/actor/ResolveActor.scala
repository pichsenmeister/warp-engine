package actor

import akka.actor._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.duration._

object ResolveActor {
    def props(channel: String) = Props(new ResolveActor(channel: String))
}


class ResolveActor(channel: String) extends Actor {

    var result = Set.empty[ActorRef]
    context.setReceiveTimeout(1 second)
    var s: Option[ActorRef] = None

    def receive = {
        case ActorIdentity(_, refOption) =>
            refOption foreach { result += _ }
        case "resolve" =>
            s = Some(sender())
            val actor = Akka.system.actorSelection("user/*/" + channel)
            actor ! Identify(None)
        case ReceiveTimeout =>
            s.foreach(_ ! result)
            context.stop(self)
    }


}
