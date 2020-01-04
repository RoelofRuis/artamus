package midi.v2

package object api {

  sealed trait MidiException extends Exception
  final case object NonExistingDevice extends MidiException
  final case class InitializationException(cause: Throwable) extends MidiException

  type MidiIO[A] = Either[MidiException, A]

  final case class ReadAction(shouldKeep: Boolean, shouldContinue: Boolean)

}
