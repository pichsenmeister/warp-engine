package actors

import akka.actor._
import play.api.Application
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue

import scala.reflect.ClassTag

/**
 * Integration between Play WebSockets and actors
 */
object ChannelActor {

    /**
     * Create a WebSocket that will pass messages to/from the actor created by the given props
     *
     * The passed in function allows the creation of an actor based on the request.  It should return a function that
     * will take the upstream actor, and return props for creating a new actor to receive WebSocket messages.  Messages
     * can be sent to the WebSocket by sending messages to the upstream actor, and the WebSocket can be closed either
     * by stopping the upstream handler, or by this actor stopping itself.
     *
     * @param f A function that takes the request header, and returns a function of the upstream actor to send
     *          messages to properties to receive messages from.
     */
    def actorOf(f: ActorRef => Props, channel: String, token: String)(implicit app: Application) = {
        ChannelExtension(Akka.system).actor ! RequestActor.Connect(channel, token, f)
    }

    /**
     * The actor that supervises and handles all messages to/from the WebSocket actor.
     */
    private class ChannelActorSupervisor[M](createHandler: ActorRef => Props, token: String)
                                             (implicit messageType: ClassTag[M]) extends Actor {

        // The actor to handle the WebSocket
        val requestActor = context.watch(context.actorOf(createHandler(self), token))

        println("requestActor in supervisor"+requestActor)

        def receive = {
            case msg: JsValue => println("message in request actor received"+msg)
        }

    }

    private object RequestActor {

        /**
         * Connect an actor to the WebSocket on the end of the given enumerator/iteratee.
         *
         * @param channel the channel. Used to name the channel actor.
         * @param requestId The requestId. Used to name the actor.
         * @param createHandler A function that creates a handler to handle the websocket, given an actor to send messages
         *                      to.
         */
        case class Connect(channel: String, requestId: String, createHandler: ActorRef => Props)
    }

    /**
     * The actor responsible for creating all web sockets
     */
    private class RequestActor extends Actor {
        import RequestActor._

        def receive = {
            case c @ Connect(channel, requestId, createHandler) =>
                context.actorOf(Props(new ChannelActorSupervisor(createHandler, channel)), requestId)
        }
    }

    /**
     * The extension for managing WebSockets
     */
    private object ChannelExtension extends ExtensionId[ChannelExtension] {
        def createExtension(system: ExtendedActorSystem) = {
            new ChannelExtension(system.actorOf(Props(new RequestActor), "channels"))
        }
    }

    private class ChannelExtension(val actor: ActorRef) extends Extension


}