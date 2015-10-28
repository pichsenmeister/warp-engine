package controllers

import actor.{Push, WarpActor}
import com.typesafe.plugin._
import helpers.Hash
import models.{WebHook, AuthToken}
import org.sedis.Dress
import play.api.{Logger, Play}
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc._
import scala.concurrent.Future

object Application extends Controller {

    val authenticate: Boolean =
        Play.current.configuration.getBoolean("auth.active").getOrElse(false)
    val cookieName: String =
        Play.current.configuration.getString("auth.cookie").getOrElse("auth-token")
    val cookieExpiration: Int =
        Play.current.configuration.getInt("auth.expiration").getOrElse(60)
    val hookExpiration: Int =
        Play.current.configuration.getInt("webhook.expiration").getOrElse(3600)

    def index = Action {
        Ok("Accelerating to Warp 6, Captain.")
    }

    def socket = WebSocket.tryAcceptWithActor[JsValue, JsValue] { request =>
        val pool = use[RedisPlugin].sedisPool

        val authToken: Option[String] = authenticate match {
            case true =>
                pool.withJedisClient { client =>
                    for {
                        cookie <- request.cookies.get(cookieName)
                        token <- Dress.up(client).get(cookie.value)
                    } yield token
                }
            case _ => Some("CaptainKirk")
        }

        Future.successful(authToken match {
            case None => Left(Forbidden)
            case Some(_) => Right(WarpActor.props(Hash.md5("Scotty-"+request.id.toString)))
        })
    }

    def push(channel: String) = Action(parse.json) { implicit request =>
        val json: JsValue = request.body
        Push.send(channel, json)
        Accepted
    }

    def hook(channel: String) = CORSAction(parse.json) { implicit request =>
        val hook: Option[WebHook] = request.body.asOpt[WebHook]
        hook match {
            case Some(h) =>
                Logger.debug("set hook "+h.hook)
                val pool = use[RedisPlugin].sedisPool
                pool.withJedisClient { client =>
                    h.duration match {
                        case Some(duration) => Dress.up(client).setex(channel, duration, h.hook)
                        case _ => Dress.up(client).set(channel, h.hook)
                    }
                }
                Accepted
            case _ => UnprocessableEntity
        }
    }

    def auth = CORSAction(parse.json) { implicit request =>
        val auth: Option[AuthToken] = request.body.asOpt[AuthToken]
        auth match {
            case Some(t) =>
                val duration: Int = t.duration.getOrElse(cookieExpiration)*60
                val now: String = (System.currentTimeMillis()/1000).toString
                val pool = use[RedisPlugin].sedisPool
                pool.withJedisClient { client =>
                    Dress.up(client).setex(t.authToken, duration, now)
                }
                Accepted
            case _ => UnprocessableEntity
        }
    }

}