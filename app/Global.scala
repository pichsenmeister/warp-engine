
import actor.HookActor
import akka.actor.{Props, ActorRef}
import play.api.GlobalSettings
import play.api.libs.concurrent.Akka

object Global extends GlobalSettings {

    override def onStart(app: play.api.Application) {
        lazy val hook: ActorRef = Akka.system(app).actorOf(Props[HookActor], "hook")
        println(hook)
    }
}