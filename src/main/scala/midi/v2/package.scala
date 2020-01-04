package midi

package object v2 {

  sealed trait MidiException extends Exception
  final case object NonExistingDevice extends MidiException
  final case class InitializationException(cause: Throwable) extends MidiException

  final case class ReadAction(keepElement: Boolean, continue: Boolean)

}
