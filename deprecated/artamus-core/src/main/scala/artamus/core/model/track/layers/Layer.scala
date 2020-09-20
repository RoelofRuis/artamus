package artamus.core.model.track.layers

import java.util.UUID

final case class Layer(
  data: LayerData,
  visible: Boolean = true,
)

object Layer {

  final case class LayerId(id: UUID = UUID.randomUUID())

}