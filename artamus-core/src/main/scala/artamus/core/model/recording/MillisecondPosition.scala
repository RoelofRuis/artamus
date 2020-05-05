package artamus.core.model.recording

final case class MillisecondPosition(v: Long)

object MillisecondPosition {

  def fromMicroseconds(microseconds: Long): MillisecondPosition = MillisecondPosition(microseconds / 1000)

}