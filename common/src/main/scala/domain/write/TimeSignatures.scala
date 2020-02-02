package domain.write

import domain.math.temporal.Position
import domain.primitives.TimeSignature

import scala.collection.immutable.SortedMap

final case class TimeSignatures private (
  timeSignatures: SortedMap[Position, TimeSignature]
) {

  def writeTimeSignature(pos: Position, ts: TimeSignature): TimeSignatures = copy(timeSignatures = timeSignatures.updated(pos, ts))

}

object TimeSignatures {

  def apply(): TimeSignatures = {
    new TimeSignatures(SortedMap(Position.ZERO -> TimeSignature.`4/4`))
  }

}
