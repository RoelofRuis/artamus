package artamus.core.model.track.layers

import artamus.core.math.temporal.Position
import artamus.core.model.primitives.Metre
import artamus.core.model.track.{Degrees, Metres}

final case class DegreeLayer(
  metres: Metres = Metres(),
  degrees: Degrees = Degrees(),
) extends LayerData {

  def writeMetre(pos: Position, metre: Metre): DegreeLayer = copy(
    metres = metres.writeMetre(pos, metre)
  )

}
