package music.domain.write.track

import music.math.temporal.Position
import music.domain.primitives.{TimeSignature, TimeSignatureDivision}

import scala.collection.immutable.SortedMap

final case class TimeSignatures private (
  timeSignatures: SortedMap[Position, TimeSignature]
) {

  def writeTimeSignature(pos: Position, ts: TimeSignature): TimeSignatures = copy(timeSignatures = timeSignatures.updated(pos, ts))

}

object TimeSignatures {

  def apply(): TimeSignatures = {
    new TimeSignatures(SortedMap(Position.ZERO -> TimeSignature(TimeSignatureDivision.`4/4`)))
  }

}
