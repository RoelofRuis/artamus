package protocol

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
    queryDispatcher: Dispatcher[Query],
    eventSubscriber: Subscriber[String, Event, Unit]
  ) {

    def subscribe(connectionId: String): Unit = {
      eventSubscriber.subscribe(connectionId, EventResponse(_))
    }

    def unsubscribe(connectionId: String): Unit = {
      eventSubscriber.unsubscribe(connectionId)
    }

    def handleRequest(request: Object): DataResponse = {
      val result = tryRead[ServerRequest](request).toEither match {
        case Right(CommandRequest(command)) =>
          commandDispatcher.handle(command) match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for command [$command]")
          }

        case Right(QueryRequest(query)) =>
          queryDispatcher.handle(query) match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for query [$query]")
          }

        case Left(ex) => Left(s"Unable to determine message type. [$ex]")
      }
      DataResponse(result)
    }

    private def tryRead[A](obj: Object): Try[A] = Try { obj.asInstanceOf[A] }
  }

}
