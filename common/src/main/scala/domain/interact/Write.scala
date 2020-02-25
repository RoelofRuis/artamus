package domain.interact

import domain.math.temporal.Position
import domain.primitives.{Key, NoteGroup, TimeSignature}

object Write {

  final case object NewWorkspace extends Command
  final case class WriteNoteGroup(group: NoteGroup) extends Command
  final case class WriteKey(position: Position, key: Key) extends Command
  final case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  final case object AnalyseChords extends Command

}
