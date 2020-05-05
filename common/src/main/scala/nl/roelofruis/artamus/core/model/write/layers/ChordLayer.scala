package nl.roelofruis.artamus.core.model.write.layers

import domain.math.temporal.Position
import domain.primitives.{Key, Metre}
import nl.roelofruis.artamus.core.model.write.{Chords, Keys, Metres}

final case class ChordLayer(
  metres: Metres = Metres(),
  keys: Keys = Keys(),
  chords: Chords = Chords(),
) extends LayerData {

  def writeMetre(pos: Position, metre: Metre): ChordLayer = copy(
    metres = metres.writeMetre(pos, metre)
  )

  def writeKey(pos: Position, key: Key): ChordLayer = copy(
    keys = keys.writeKey(pos, key)
  )

}
