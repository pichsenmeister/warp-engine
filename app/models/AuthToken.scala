package models

import play.api.libs.json._

case class AuthToken(
    authToken: String,
    duration: Int)

object AuthToken {
    implicit val authTokenFormat: Format[AuthToken] = Json.format[AuthToken]
}