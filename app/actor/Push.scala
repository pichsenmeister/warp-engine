package actor

import akka.actor.ActorSelection
import akka.pattern.ask
import akka.util.Timeout
import org.joda.time.DateTime
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsValue, Json}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import play.api.Play.current

object Push {

    implicit val timeout = Timeout(5 seconds)

    def send(channel: String, json: JsValue): Unit = {
        val value: JsValue = Json.obj("channel" -> channel, "msg" -> json, "timestamp" -> DateTime.now().getMillis())
        val actor: ActorSelection = Akka.system.actorSelection("user/*/"+channel)
        (actor ? value).mapTo[JsValue] recover {
            case e: Throwable =>
                Json.obj("error" -> "no user connected")
        }
    }

}
