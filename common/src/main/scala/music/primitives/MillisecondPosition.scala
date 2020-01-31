package music.primitives

final case class MillisecondPosition(v: Long)

object MillisecondPosition {

  def fromMicroseconds(microseconds: Long): MillisecondPosition = MillisecondPosition(microseconds / 1000)

}