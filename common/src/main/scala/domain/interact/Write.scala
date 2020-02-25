package domain.interact

import domain.math.temporal.Position
import domain.primitives.{Key, NoteGroup, TimeSignature}
import domain.write.layers.Layer.LayerId

object Write {

  final case object NewWorkspace extends Command
  final case class WriteNoteGroup(group: NoteGroup) extends Command
  final case class WriteKey(position: Position, key: Key) extends Command
  final case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  final case object GetLayers extends Query { type Res = Map[Int, (LayerId, Boolean)] }
  final case class SetLayerVisibility(layer: LayerId, isVisible: Boolean) extends Command

  final case object AnalyseChords extends Command

}
