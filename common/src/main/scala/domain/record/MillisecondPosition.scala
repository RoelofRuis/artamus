package domain.record

final case class MillisecondPosition(v: Long)

object MillisecondPosition {

  def fromMicroseconds(microseconds: Long): MillisecondPosition = MillisecondPosition(microseconds / 1000)

}