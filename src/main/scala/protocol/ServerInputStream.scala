package protocol

import java.io.ObjectInputStream

import protocol.MessageTypes._

import scala.util.Try

private[protocol] class ServerInputStream(in: ObjectInputStream) {

  def readNext(bindings: ServerBindings): Either[StreamException, Any] = {
    readObject[ServerRequest]().toEither match {
        case Right(CommandRequest) =>
          readObject[Command]().toEither match {
            case Right(command) => Right(bindings.commandHandler.handle(command))
            case Left(ex) => Left(s"Unable to decode Command message. [$ex]")
          }

        case Right(ControlRequest) =>
          readObject[Control]().toEither match {
            case Right(control) => Right(bindings.controlHandler.handle(control))
            case Left(ex) => Left(s"Unable to decode control message. [$ex]")
          }

        case Right(QueryRequest) =>
          readObject[Query]().toEither match {
            case Right(query) => Right(bindings.queryHandler.handle(query))
            case Left(ex) => Left(s"Unable to decode query message. [$ex]")
          }

        case Left(ex) => Left(s"Unable to determine message type. [$ex]")
    }
  }

  private def readObject[A](): Try[A] = Try(in.readObject().asInstanceOf[A])

}
