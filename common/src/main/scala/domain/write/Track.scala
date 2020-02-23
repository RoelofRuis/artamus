package domain.write

import java.util.UUID

import domain.math.temporal.Position
import domain.write.layers.{ChordLayer, Layer, LayerData, NoteLayer, RhythmLayer}
import domain.write.Track.TrackId
import domain.primitives.{Key, NoteGroup, TimeSignature}

final case class Track (
  id: TrackId = TrackId(),
  layers: List[Layer] = List()
) {

  def writeTimeSignature(pos: Position, timeSignature: TimeSignature): Track = mapLayerData {
    case x: ChordLayer => x.writeTimeSignature(pos, timeSignature)
    case x: NoteLayer => x.writeTimeSignature(pos, timeSignature)
    case x: RhythmLayer => x.writeTimeSignature(pos, timeSignature)
    case x => x
  }

  def writeKey(pos: Position, key: Key): Track = mapLayerData {
    case x: NoteLayer => x.writeKey(pos, key)
  }

  def writeNoteGroup(noteGroup: NoteGroup): Track = mapLayerData {
    case x: NoteLayer => x.writeNoteGroup(noteGroup)
    case x: RhythmLayer => x.writeNoteGroup(noteGroup)
    case x => x
  }

  def addLayerData(layer: LayerData): Track = copy(layers = layers :+ Layer(layer))

  def layerData: List[LayerData] = layers.map(_.data)

  private def mapLayerData(f: LayerData => LayerData): Track = copy (
    layers = layers.map(layer => layer.copy(data = f(layer.data)))
  )
}

object Track {

  def apply(layer: LayerData): Track = Track(layers = List(Layer(layer)))

  def empty: Track = apply()

  final case class TrackId(id: UUID = UUID.randomUUID())

}
