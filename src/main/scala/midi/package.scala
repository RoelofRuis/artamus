import scala.util.{Failure, Success, Try}

package object midi {

  type DeviceHash = String

  final case class MidiIOException(cause: Throwable) extends Exception

  type MidiIO[A] = Either[MidiIOException, A]

  object MidiIO {
    def wrap[E <: Throwable, A](e: Either[E, A]): MidiIO[A] = {
      e match {
        case Right(value) => Right(value)
        case Left(ex) => Left(MidiIOException(ex))
      }
    }
    def apply[A](x: => A): MidiIO[A] = {
      Try { x } match {
        case Success(value) => Right(value)
        case Failure(ex) => Left(MidiIOException(ex))
      }
    }
    def failure[A](ex: Throwable): MidiIO[A] = Left(MidiIOException(ex))
  }

}
