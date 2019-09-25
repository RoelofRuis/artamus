package protocol

import protocol.MessageTypes._
import pubsub.{Dispatcher, Subscriber}

import scala.util.Try

package object server {

  trait ServerInterface {

    def accept(): Unit

    def shutdown(): Unit

  }

  // TODO: clean this up!
  final case class ServerBindings(
    commandDispatcher: Dispatcher[Command],
    controlDispatcher: Dispatcher[Control],
    queryDispatcher: Dispatcher[Query],
    eventSubscriber: Subscriber[String, Event, Unit]
  ) {
    def handleRequest(request: Object, payload: Object): List[Object] = {
      val response = tryRead[ServerRequest](request).toEither match {
        case Right(CommandRequest) =>
          tryRead[Command](payload).toEither match {
            case Right(command) =>
              commandDispatcher.handle(command) match {
                case Some(res) => Right(res)
                case None => Left(s"No handler defined for command [$command]")
              }
            case Left(ex) => Left(s"Unable to decode Command message. [$ex]")
          }

        case Right(ControlRequest) =>
          tryRead[Control](payload).toEither match {
            case Right(control) =>
              controlDispatcher.handle(control) match {
                case Some(res) => Right(res)
                case None => Left(s"No handler defined for control [$control]")
              }
            case Left(ex) => Left(s"Unable to decode control message. [$ex]")
          }

        case Right(QueryRequest) =>
          tryRead[Query](payload).toEither match {
            case Right(query) =>
              queryDispatcher.handle(query) match {
                case Some(res) => Right(res)
                case None => Left(s"No handler defined for query [$query]")
              }
            case Left(ex) => Left(s"Unable to decode query message. [$ex]")
          }

        case Left(ex) => Left(s"Unable to determine message type. [$ex]")
      }
      List(DataResponse, response)
    }

    private def tryRead[A](obj: Object): Try[A] = Try { obj.asInstanceOf[A] }

    def writeEvent(msg: Any): List[Any] = List(EventResponse, msg)
  }

}
