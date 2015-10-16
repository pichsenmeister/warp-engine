package actor

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.plugin._
import messages.{ClientMessage, Unsubscribe, Subscribe}
import org.joda.time.DateTime
import org.sedis.Dress
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsValue, Json}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import scala.concurrent.duration._
import play.api.Play.current

object HookActor {
    def props() = Props(new HookActor())
}

class HookActor() extends Actor {

    def receive = {
        case (channel: String, sub: Subscribe) =>
            Logger.debug("received in HookActor ("+channel+"): "+Json.toJson(sub))
            sendToHook(channel, Json.obj("sub" -> Json.toJson(sub)))
        case (channel: String, unsub: Unsubscribe) =>
            Logger.debug("received in HookActor ("+channel+"): "+Json.toJson(unsub))
            sendToHook(channel, Json.obj("unsub" -> Json.toJson(unsub)))
        case (channel: String, msg: ClientMessage) =>
            Logger.debug("received in HookActor ("+channel+"): "+Json.toJson(msg))
            sendToHook(channel, Json.toJson(msg))
    }

    private def sendToHook(channel: String, msg: JsValue): Unit = {
        println("actually send to hook")
        val pool = use[RedisPlugin].sedisPool
        pool.withJedisClient { client =>
            val hook: Option[String] = Dress.up(client).get(channel)
            hook.foreach(h => WS.url(h).withHeaders("Content-Type" -> "application/json").post(msg.toString()))
        }
    }

    override def postStop = {
        Logger.debug("kill HookActor: "+self)
    }



}
