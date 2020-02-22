package domain.write.layers

final case class Layer(
  data: LayerData,
  visible: Boolean = true,
)
