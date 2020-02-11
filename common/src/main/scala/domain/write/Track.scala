package domain.write

import java.util.UUID

import domain.math.temporal.Position
import domain.write.Layers.{ChordLayer, Layer, NoteLayer, RhythmLayer}
import domain.write.Track.TrackId
import domain.primitives.{Key, NoteGroup, TimeSignature}

final case class Track (
  id: TrackId = TrackId(),
  layers: List[Layer] = List()
) {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track = copy(
    layers = layers.map {
      case x: ChordLayer => x.writeTimeSignature(pos, timeSignature)
      case x: NoteLayer => x.writeTimeSignature(pos, timeSignature)
      case x: RhythmLayer => x.writeTimeSignature(pos, timeSignature)
    }
  )

  def writeKey(pos: Position, key: Key): Track = copy(
    layers = layers.map {
      case x: NoteLayer => x.writeKey(pos, key)
      case x => x
    }
  )

  def addLayer(layer: Layer): Track = copy(
    layers = layers :+ layer
  )

  def writeNoteGroup(noteGroup: NoteGroup): Track = copy(
    layers = layers.map {
      case x: NoteLayer => x.writeNoteGroup(noteGroup)
      case x: RhythmLayer => x.writeNoteGroup(noteGroup)
      case x => x
    }
  )
}

object Track {

  def apply(layer: Layer): Track = Track(layers = List(layer))
  def emptyNotes: Track = Track(layers = List(NoteLayer()))
  def emptyRhythm: Track = Track(layers = List(RhythmLayer()))

  final case class TrackId(id: UUID = UUID.randomUUID())

}
