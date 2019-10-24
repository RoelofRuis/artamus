package server.interpret.lilypond

import music.primitives._
import music.symbol.collection.Track
import music.symbol.{Key, Note, TimeSignature}

import scala.annotation.tailrec

class StaffIterator(track: Track) extends ContentIterator {

  import music.analysis.TwelveToneEqualTemprament._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.getSymbolTrack[TimeSignature]
  private val keys = track.getSymbolTrack[Key]
  private val notes = track.getSymbolTrack[Note]

  @tailrec
  private def readNext(pos: Position): Option[(Position, String)] = {
    val nextNotes = notes.readNext(pos)
    if (nextNotes.isEmpty) None
    else {
      val nextPos = nextNotes.head.position
      nextNotes.map(_.symbol).toLilypond match {
        case Some(lilyString) => Some((nextPos, lilyString))
        case None => readNext(nextPos)
      }
    }
  }

  def stream: Stream[String] = { // TODO: make iterator
    val pos = Position.zero // TODO: make argument later
    val initialTimeSignature = timeSignatures
      .readFirstAt(pos)
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    val initialKey = keys
      .readFirstAt(pos)
      .map(_.symbol)
      .getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

    val initialNotes = notes.readAt(pos).map(_.symbol).toLilypond.getOrElse("")

    def loop(pos: Position, timeSignature: TimeSignature, key: Key): Stream[String] = {
      readNext(pos) match {
        case None => Stream.empty
        case Some((nextPos, lilyString)) =>
          // TODO: check of rusten tussengevoegd moeten worden (DIT IS EEN LILYPOND DING!)
          lilyString #:: loop(nextPos, timeSignature, key)
      }
    }

    initialTimeSignature.toLilypond.get #:: initialKey.toLilypond.get #:: initialNotes #:: loop(pos, initialTimeSignature, initialKey)
  }

}
