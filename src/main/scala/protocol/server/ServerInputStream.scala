package protocol.server

import java.io.ObjectInputStream

import protocol.MessageTypes._
import protocol.{Command, Control, Query}

import scala.util.Try

private[protocol] class ServerInputStream(in: ObjectInputStream) {

  def readNext(bindings: ServerBindings): Either[StreamException, Any] = {
    readObject[ServerRequest]().toEither match {
        case Right(CommandRequest) =>
          readObject[Command]().toEither match {
            case Right(command) =>
              bindings.commandDispatcher.handle(command) match {
                case Some(res) => Right(res)
                case None => Left(s"No handler defined for command [$command]")
              }
            case Left(ex) => Left(s"Unable to decode Command message. [$ex]")
          }

        case Right(ControlRequest) =>
          readObject[Control]().toEither match {
            case Right(control) =>
              bindings.controlDispatcher.handle(control) match {
                case Some(res) => Right(res)
                case None => Left(s"No handler defined for control [$control]")
              }
            case Left(ex) => Left(s"Unable to decode control message. [$ex]")
          }

        case Right(QueryRequest) =>
          readObject[Query]().toEither match {
            case Right(query) =>
              bindings.queryDispatcher.handle(query) match {
                case Some(res) => Right(res)
                case None => Left(s"No handler defined for query [$query]")
              }
            case Left(ex) => Left(s"Unable to decode query message. [$ex]")
          }

        case Left(ex) => Left(s"Unable to determine message type. [$ex]")
    }
  }

  private def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

}
