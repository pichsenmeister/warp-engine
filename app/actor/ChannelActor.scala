package actor

import akka.actor.{Props, Actor, ActorRef}
import messages.{ClientMessage, Subscribe, Unsubscribe}
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.libs.json.{Json, JsValue}
import play.api.Play.current

object ChannelActor {
    def props(out: ActorRef) = Props(new ChannelActor(out: ActorRef))
}

class ChannelActor(out: ActorRef) extends Actor {

    // optional data at subscription
    var data: Option[JsValue] = None

    def receive = {
        case subscribe: Subscribe =>
            Logger.debug("subscribed in ChannelActor: "+Json.toJson(subscribe).toString())
            data = subscribe.data
            out ! Json.obj("sub" -> Json.toJson(subscribe))
        case unsubscribe: Unsubscribe =>
            Logger.debug("unsubscribed in ChannelActor: "+Json.toJson(unsubscribe).toString())
            out ! Json.obj("unsub" -> Json.toJson(unsubscribe))
        case msg: ClientMessage =>
            Logger.debug("received in ChannelActor: "+Json.toJson(msg))
            out ! Json.toJson(msg.copy(data = data))
    }

    override def postStop = {
        Logger.debug("kill ChannelActor: "+self)
        val token: String = self.path.parent.name
        val unsub: Unsubscribe = Unsubscribe(token, self.path.name, DateTime.now().getMillis(), data)

        val actor = Akka.system.actorSelection("user/*/"+self.path.name)
        actor ! unsub

        val hook = Akka.system.actorSelection("user/hook")
        hook ! (self.path.name, unsub)
    }

}
