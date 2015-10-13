package models

import play.api.libs.json._

case class WebHook(
    hook: String,
    duration: Option[Int])

object WebHook {
    implicit val webHookFormat: Format[WebHook] = Json.format[WebHook]
}