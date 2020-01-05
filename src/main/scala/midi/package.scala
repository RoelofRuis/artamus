import scala.util.{Failure, Success, Try}

package object midi {

  type DeviceHash = String

  sealed trait MidiException extends Exception
  final case object NonExistingDevice extends MidiException
  final case class InitializationException(cause: Throwable) extends MidiException
  final case class CommunicationException(cause: Throwable) extends MidiException

  type MidiIO[A] = Either[MidiException, A]

  object MidiIO {
    def apply[A](x: => A): MidiIO[A] = {
      Try { x } match {
        case Success(value) => MidiIO.of(value)
        case Failure(ex) => MidiIO.unableToInitialize(ex)
      }
    }
    def of[A](a: A): MidiIO[A] = Right(a)
    def ok: MidiIO[Unit] = Right(())
    def unableToInitialize[A](ex: Throwable): MidiIO[A] = Left(InitializationException(ex))
    def nonExistingDevice[A]: MidiIO[A] = Left(NonExistingDevice)
    def communicationException[A](ex: Throwable): MidiIO[A] = Left(CommunicationException(ex))
  }
  // TODO: add MidiIO companion object for cleaner creation of instances!

}
