package domain.write

import domain.primitives.NoteGroup
import domain.write.Layers.NoteLayer

import scala.annotation.tailrec

/** Methods to blend two tracks A and B in a variety of ways. */
object TrackBlending {

  sealed trait TrackBlendMode

  /** Keep A, discard B */
  final case object Keep extends TrackBlendMode
  /** Keep B, discard A */
  final case object Replace extends TrackBlendMode
  /** If both tracks have just a note layer, combine both. */
  final case object OverlayNotes extends TrackBlendMode

  final case class TrackBlendException(mode: TrackBlendMode, message: String) extends Exception

  def blend(trackA: Track, trackB: Track, mode: TrackBlendMode): Either[TrackBlendException, Track] = {
    mode match {
      case Keep => Right(trackA)
      case Replace => Right(trackB)
      case OverlayNotes =>
        val res = for {
          trackANotes <- trackA.layers.collectFirst { case l: NoteLayer => l }
          trackBNotes <- trackB.layers.collectFirst { case l: NoteLayer => l }
        } yield {
          val combinedNotes = blendNotes(trackANotes.notes.readGroupsList(), trackBNotes.notes.readGroupsList(), List())
            .foldLeft(Notes()) { case (acc, group) => acc.writeNoteGroup(group) }

          Track(NoteLayer(trackANotes.timeSignatures, trackANotes.keys, combinedNotes))
        }

        res match {
          case None => Left(TrackBlendException(OverlayNotes, "Tracks should both have note layers"))
          case Some(t) => Right(t)
        }
    }
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
