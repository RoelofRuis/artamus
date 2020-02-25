package domain.write

import java.util.UUID

import domain.write.Track.TrackId
import domain.write.layers.Layer.LayerId
import domain.write.layers._

import scala.collection.immutable.ListMap
import scala.reflect.ClassTag

final case class Track (
  id: TrackId = TrackId(),
  layers: ListMap[LayerId, Layer] = ListMap()
) {

  def appendLayerData(layer: LayerData): Track = copy(layers = layers.updated(LayerId(), Layer(layer)))

  // TODO: review if this function should ever be necessary (better to let caller provide layer ID)
  def readFirstLayer[A <: LayerData : ClassTag]: Option[A] = readLayers.collectFirst { case a: A => a }

  def readLayers: List[LayerData] = layers.map { case (_, layer) => layer.data }.toList

  def updateLayer(id: LayerId, f: Layer => Layer): Track = copy (
    layers = layers.get(id) match {
      case None => layers
      case Some(layer) => layers.updated(id, f(layer))
    }
  )

  def mapLayerData(f: PartialFunction[LayerData, LayerData]): Track = copy (
    layers = layers.map { case (ref, layer) =>
      if (f.isDefinedAt(layer.data)) (ref, layer.copy(data = f(layer.data)))
      else (ref, layer)
    }
  )
}

object Track {

  def apply(layer: LayerData): Track = Track(layers = ListMap(LayerId() -> Layer(layer)))

  def empty: Track = Track(layers = ListMap())

  final case class TrackId(id: UUID = UUID.randomUUID())

}
