package controllers

import play.api.Play
import play.api.mvc._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._

object Actions extends Controller {

    def options(path: String) = CORSAction {
        Ok("")
    }

}

object CORSAction extends ActionBuilder[Request] {

    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
        val host: String = request.host

        val cors: Option[String] = for {
            domains <- Play.current.configuration.getStringList("cors.domains")
            origin <- domains.asScala.find(p => p == host || p == "*")
        } yield origin

        val active: Boolean = Play.current.configuration.getBoolean("cors.active").getOrElse(false)

        (cors, active) match {
            case (Some(d), true) if d == "*" => CORSHeaders.withHeaders(block(request), "*")
            case (Some(d), true) => CORSHeaders.withHeaders(block(request), "http://"+d)
            case (_, true) => CORSHeaders.withHeaders(block(request), "None")
            case _ => block(request)
        }
    }
}


object CORSHeaders {

    def withHeaders(r: Future[Result], header: String): Future[Result] = {

        val maxAge: Int = Play.current.configuration.getInt("cors.max-age").getOrElse(86400)

        r.map(result => result.withHeaders("Access-Control-Allow-Origin" -> header,
            "Access-Control-Allow-Credentials" -> "true",
            "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
            "Access-Control-Allow-Headers" -> "Content-Type, X-Requested-With, Accept",
            "Access-Control-Max-Age" -> maxAge.toString,
            "Content-Type" -> "application/json; charset=utf-8")
        )

    }

}