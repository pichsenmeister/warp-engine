package messages

import play.api.libs.json.{Json, Format, JsValue}

case class ClientMessage(token: String, channel: String, msg: JsValue, timestamp: Long, data: Option[JsValue] = None)

object ClientMessage {
    implicit val clientMessageFormat: Format[ClientMessage] = Json.format[ClientMessage]
}
