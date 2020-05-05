package nl.roelofruis.artamus.core.model.write

import nl.roelofruis.math.temporal.Position
import nl.roelofruis.artamus.core.model.primitives.{Note, NoteGroup}

import scala.collection.immutable.SortedMap

final case class Voice private (
  notes: SortedMap[Position, NoteGroup]
) {

  def readGroupsList(): List[NoteGroup] = notes.values.toList
  def readGroups(): Iterator[NoteGroup] = notes.valuesIterator
  def read(): Iterator[Note] = notes.valuesIterator.flatMap(_.notes)

  def writeNoteGroup(noteGroup: NoteGroup): Voice = copy(
    notes = notes.updated(noteGroup.window.start, noteGroup)
  )

}

object Voice {

  def apply(): Voice = new Voice(SortedMap())

  final case class VoiceId(id: Int) extends AnyVal
}