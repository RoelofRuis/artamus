package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{Key, Metre}
import domain.write.{Chords, Keys, Metres}

final case class ChordLayer(
  timeSignatures: Metres = Metres(),
  keys: Keys = Keys(),
  chords: Chords = Chords(),
) extends LayerData {

  def writeMetre(pos: Position, metre: Metre): ChordLayer = copy(
    timeSignatures = timeSignatures.writeMetre(pos, metre)
  )

  def writeKey(pos: Position, key: Key): ChordLayer = copy(
    keys = keys.writeKey(pos, key)
  )

}
