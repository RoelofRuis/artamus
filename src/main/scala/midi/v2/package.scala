package midi

package object v2 {

  type DeviceHash = String

  sealed trait MidiException extends Exception
  final case object NonExistingDevice extends MidiException
  final case class InitializationException(cause: Throwable) extends MidiException
  final case class CommunicationException(cause: Throwable) extends MidiException

  type MidiIO[A] = Either[MidiException, A]

  // TODO: add MidiIOOps for easy manipulation of results
  // TODO: add MidiIO companion object for cleaner creation of instances!

}
