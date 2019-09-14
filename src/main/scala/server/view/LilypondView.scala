package server.view

import java.io._

import javax.inject.Inject
import music.interpret.NaivePitchSpelling
import music.write.LilypondFormatDummy
import music.{MidiPitch, Note, Position, TimeSignature}
import protocol.Event
import pubsub.EventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}

// TODO: Separate pitch analyis and lilypond and clean up!
class LilypondView @Inject() (
  eventBus: EventBus[Event],
  trackState: TrackState
) {

  eventBus.subscribe("pitch-spelling", {
    case TrackSymbolsUpdated =>
      val currentState = trackState.getTrack

      val notes = currentState
        .getAllStackedSymbols[Note[MidiPitch]]
        .map { case (_, stackedNotes) => stackedNotes.head }
      val scientificPitch = NaivePitchSpelling.interpret(notes.map(_.pitch))

      val spelledNotes = notes.zip(scientificPitch).map { case (note, sp) => Note(note.duration, sp) }
      val lilyString = LilypondFormatDummy.notesToLilypond(spelledNotes)

      val timeSignature = currentState.getSymbolAt[TimeSignature](Position.zero).map(LilypondFormatDummy.timeSignatureToLily)
      val lilyFile = LilypondFormatDummy.compileFile(lilyString, timeSignature)

      build(lilyFile)
      ()
    case _ => ()
  })

  def build(contents: String): Unit = {
    try {
      val file = new File("data/test.ly")

      val writer = new PrintWriter(file)
      writer.write(contents)
      writer.close()

      println(s"Running lilypond...")
      import sys.process._
      val result = s"lilypond -fpng -odata ${file.getAbsolutePath}".!!
      println(s"Lilypond completed\n$result\n")
    } catch {
      case ex: IOException => println(s"IO exception [$ex]")
      case ex: Throwable => println(s"Other exception [$ex]")
    }
  }

}
