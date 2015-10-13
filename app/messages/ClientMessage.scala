package messages

import play.api.libs.json.JsValue

case class ClientMessage(token: String, json: JsValue)


