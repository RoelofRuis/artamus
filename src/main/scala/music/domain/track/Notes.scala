package music.domain.track

import music.math.temporal.Position
import music.primitives.{Note, NoteGroup}

import scala.collection.BufferedIterator
import scala.collection.immutable.SortedMap

final case class Notes private (
  notes: SortedMap[Position, NoteGroup]
) {

  def readGroups: BufferedIterator[NoteGroup] = notes.valuesIterator.buffered
  def read: BufferedIterator[Note] = notes.valuesIterator.flatMap(_.notes).buffered

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