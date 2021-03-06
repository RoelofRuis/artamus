package artamus.core.model.track.layers

import artamus.core.math.temporal.Position
import artamus.core.model.primitives.{Key, Metre}
import artamus.core.model.track.{Chords, Keys, Metres}

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
