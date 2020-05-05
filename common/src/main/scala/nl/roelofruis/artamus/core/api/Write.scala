package nl.roelofruis.artamus.core.api

import nl.roelofruis.math.temporal.Position
import nl.roelofruis.artamus.core.model.primitives.{Key, Metre, NoteGroup}
import nl.roelofruis.artamus.core.model.write.layers.Layer.LayerId

object Write {

  final case object NewWorkspace extends Command
  final case class WriteNoteGroup(group: NoteGroup) extends Command
  final case class WriteKey(position: Position, key: Key) extends Command
  final case class WriteMetre(position: Position, metre: Metre) extends Command

  @deprecated("This should be replaced by completely async handling")
  final case object GetLayers extends Query { type Res = Map[Int, (LayerId, Boolean)] }
  final case class SetLayerVisibility(layer: LayerId, isVisible: Boolean) extends Command
  final case class DeleteLayer(layer: LayerId) extends Command

  final case object AnalyseChords extends Command

}
