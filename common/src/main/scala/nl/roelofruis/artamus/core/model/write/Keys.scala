package nl.roelofruis.artamus.core.model.write

import nl.roelofruis.math.temporal.Position
import nl.roelofruis.artamus.core.model.primitives._

import scala.collection.immutable.SortedMap

final case class Keys private (
  keys: SortedMap[Position, Key]
) {

  // TODO: it should be clearer that this is always set!
  def initialKey: Key = keys.head._2

  def writeKey(pos: Position, key: Key): Keys = copy(keys = keys.updated(pos, key))

}

object Keys {

  def apply(): Keys = {
    import nl.roelofruis.artamus.core.model.write.analysis.TwelveToneTuning._
    new Keys(SortedMap(Position.ZERO -> Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR)))
  }

}