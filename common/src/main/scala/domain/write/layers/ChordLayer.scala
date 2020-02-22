package domain.write.layers

import domain.math.temporal.Position
import domain.primitives.{Key, TimeSignature}
import domain.write.{Chords, Keys, TimeSignatures}

final case class ChordLayer(
  timeSignatures: TimeSignatures = TimeSignatures(),
  keys: Keys = Keys(),
  chords: Chords = Chords(),
) extends LayerData {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): ChordLayer = copy(
    timeSignatures = timeSignatures.writeTimeSignature(pos, timeSignature)
  )

  def writeKey(pos: Position, key: Key): ChordLayer = copy(
    keys = keys.writeKey(pos, key)
  )

}
