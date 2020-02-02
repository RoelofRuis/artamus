package domain.write

import domain.math.temporal.Position
import domain.primitives.{Note, NoteGroup}

import scala.collection.immutable.SortedMap

final case class Notes private ( // TODO: see if still required
  notes: SortedMap[Position, NoteGroup]
) {

  def readGroupsList(): List[NoteGroup] = notes.values.toList
  def readGroups(): Iterator[NoteGroup] = notes.valuesIterator
  def read(): Iterator[Note] = notes.valuesIterator.flatMap(_.notes).buffered

  def writeNoteGroup(noteGroup: NoteGroup): Notes = copy(
      notes = notes.updated(noteGroup.window.start, noteGroup)
    )

  def mapNotes(f: Note => Note): Notes = copy(
      notes = notes.map {
        case (position, noteGroup) => (position, NoteGroup(noteGroup.window, noteGroup.notes.map(f)))
      }
    )

}

object Notes {

  def apply(): Notes = new Notes(SortedMap())

}