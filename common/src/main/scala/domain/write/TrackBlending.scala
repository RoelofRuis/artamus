package domain.write

import domain.write.layers.{NoteBlending, NoteLayer}

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
          trackANotes <- trackA.layerData.collectFirst { case l: NoteLayer => l }
          trackBNotes <- trackB.layerData.collectFirst { case l: NoteLayer => l }
        } yield Track(NoteBlending.blendNoteLayers(trackANotes, trackBNotes))

        res match {
          case None => Left(TrackBlendException(OverlayNotes, "Tracks should both have note layers"))
          case Some(t) => Right(t)
        }
    }
  }
}
