package domain.write

import java.util.UUID

import domain.write.Track.TrackId
import domain.write.layers.Layer.LayerId
import domain.write.layers._

final case class Track (
  id: TrackId = TrackId(),
  layers: Map[LayerId, Layer] = Map()
) {

  def appendLayerData(layer: LayerData): Track = copy(layers = layers.updated(LayerId(), Layer(layer)))

  def readLayers: List[LayerData] = layers.map { case (_, layer) => layer.data }.toList

  def mapLayerData(f: PartialFunction[LayerData, LayerData]): Track = copy (
    layers = layers.map { case (ref, layer) =>
      if (f.isDefinedAt(layer.data)) (ref, layer.copy(data = f(layer.data)))
      else (ref, layer)
    }
  )
}

object Track {

  def apply(layer: LayerData): Track = Track(layers = Map(LayerId() -> Layer(layer)))

  def empty: Track = Track(layers = Map())

  final case class TrackId(id: UUID = UUID.randomUUID())

}
