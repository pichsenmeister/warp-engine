package controllers

import actor.{Push, WarpActor}
import models.AuthToken
import org.sedis.Dress
import play.api.Play.current
import com.typesafe.plugin._
import play.api.libs.json.JsValue
import play.api.mvc._
import scala.concurrent.Future

object Application extends Controller {

    def channel(channel: String) = CORSAction {
        Ok(views.html.main(channel)).withCookies(
            new Cookie("authToken", "token2", None, "/", None, false, true))
    }

    def socket(channel: String) = WebSocket.tryAcceptWithActor[String, String] { request =>
        val pool = use[RedisPlugin].sedisPool

        val authToken = pool.withJedisClient { client =>
            for {
                cookie <- request.cookies.get("authToken")
                token <- Dress.up(client).get(cookie.value)
            } yield token
        }

        Future.successful(authToken match {
            case None => Left(Forbidden)
            case Some(_) => Right(WarpActor.props(_, channel, request.id.toString))
        })
    }

    def push(channel: String) = Action(parse.json) { implicit request =>
        val json: JsValue = request.body
       Push.send(channel, json)
        Accepted
    }

    def auth = CORSAction(parse.json) { implicit request =>
        val auth: Option[AuthToken] = request.body.asOpt[AuthToken]
        auth match {
            case Some(t) =>
                val duration: Int = t.duration*60
                val now: String = (System.currentTimeMillis()/1000).toString
                val pool = use[RedisPlugin].sedisPool
                pool.withJedisClient { client =>
                    Dress.up(client).setex(t.authToken, duration, now)
                }
                Accepted
            case _ => BadRequest
        }
    }

}