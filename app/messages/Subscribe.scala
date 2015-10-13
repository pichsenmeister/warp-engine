package messages

import play.api.libs.json.{Json, Format, JsValue}

case class Subscribe(token: String, channel: String, data: Option[JsValue] = None)

object Subscribe {
    implicit val subscribeFormat: Format[Subscribe] = Json.format[Subscribe]
}