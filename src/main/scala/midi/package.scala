import scala.util.{Failure, Success, Try}

package object midi {

  type DeviceHash = String

  final case class MidiIOException(cause: Throwable)

  type MidiIO[A] = Either[MidiIOException, A]

  object MidiIO {
    def apply[A](x: => A): MidiIO[A] = {
      Try { x } match {
        case Success(value) => Right(value)
        case Failure(ex) => Left(MidiIOException(ex))
      }
    }
    def ok: MidiIO[Unit] = Right(())
    def failure[A](ex: Throwable): MidiIO[A] = Left(MidiIOException(ex))
  }

  // TODO: add MidiIO companion object for cleaner creation of instances!

}
