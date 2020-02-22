package domain.write.layers

import domain.primitives.NoteGroup
import domain.write.Notes

import scala.annotation.tailrec

object NoteBlending {

  def blendNoteLayers(l1: NoteLayer, l2: NoteLayer): NoteLayer = {
    val combinedNotes = blendNotes(
      l1.notes.readGroupsList(),
      l2.notes.readGroupsList(),
      List()
    ).foldLeft(Notes()) { case (acc, group) => acc.writeNoteGroup(group) }

    NoteLayer(l1.timeSignatures, l1.keys, combinedNotes)
  }

  @tailrec
  private def blendNotes(groupA: List[NoteGroup], groupB: List[NoteGroup], acc: List[NoteGroup]): List[NoteGroup] = {
    (groupA, groupB) match {
      case (Nil, Nil) => acc
      case (Nil, b) =>  acc ++ b
      case (a, Nil) => acc ++ a
      case (headA :: tailA, headB :: tailB) =>
        if (headA.window == headB.window) blendNotes(tailA, tailB, acc :+ NoteGroup(headA.window, headA.notes ++ headB.notes))
        else blendNotes(tailA, tailB, acc :+ headA) // TODO: blend to multiple voices (now we lose information here)
    }
  }

}
