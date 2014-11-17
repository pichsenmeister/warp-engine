package controllers

import actor.WarpActor
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.mvc._

object Application extends Controller {

    def channel(channel: String) = Action {
        Ok(views.html.main(channel))
    }

    def socket(channel: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
        WarpActor.props(out, channel, request.id.toString)
    }

    def push(channel: String) = Action(parse.json) { implicit request =>
        val json: JsValue = request.body
        val actor = Akka.system.actorSelection("user/*/"+channel)
        actor ! json
        Accepted
    }

}