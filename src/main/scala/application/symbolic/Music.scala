package application.symbolic

object Music {

  case class MidiPitch(value: Int) extends AnyVal
  case class PositionsOccupied(value: Int) extends AnyVal
  case class NoteLength(value: Int) extends AnyVal // Any power of 2 (TODO: requires better name)
  case class Divisions(value: Int) extends AnyVal

  sealed trait GridElement

  case class Event private(midiPitch: Option[MidiPitch], positionsOccupied: PositionsOccupied) extends GridElement

  case class SubGrid private(divisions: Divisions, elements: Seq[GridElement]) extends GridElement

  case class Grid(root: SubGrid, baseNoteLength: NoteLength)

}
