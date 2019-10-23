package server.interpret.lilypond

import music.collection.{Track, TrackSymbol}
import music.primitives._
import music.symbols.{Key, Note, TimeSignature}

class StaffIterator(track: Track) {

  import music.analysis.TwelveToneEqualTemprament._
  import server.interpret.lilypond.LilypondFormat._

  private val timeSignatures = track.getSymbolTrack[TimeSignature]
  private val keys = track.getSymbolTrack[Key]
  private val notes = track.getSymbolTrack[Note]

  private def notesToLilypond(notes: Seq[TrackSymbol[Note]], key: Key): Stream[String] = {
    Stream(notes.map(_.symbol).toLilypond)
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

    val initialNotes = notes.readAt(pos)

    def loop(pos: Position, timeSignature: TimeSignature, key: Key): Stream[String] = {
      val nextNotes = notes.readNext(pos)
      if (nextNotes.isEmpty) Stream.empty
      else {
        val nextPos = nextNotes.head.position
        // TODO: check of rusten tussengevoegd moeten worden (DIT IS EEN LILYPOND DING!)
        notesToLilypond(nextNotes, key) #::: loop(nextPos, timeSignature, key)
      }
    }

    initialTimeSignature.toLilypond #:: initialKey.toLilypond #:: notesToLilypond(initialNotes, initialKey) #::: loop(pos, initialTimeSignature, initialKey)
  }

}
