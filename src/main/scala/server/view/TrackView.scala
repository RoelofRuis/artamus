package server.view

import java.io._

import javax.inject.Inject
import music.{MidiPitch, Note, Position}
import music.interpret.NaivePitchSpelling
import music.write.LilypondFormatDummy
import protocol.Event
import pubsub.EventBus
import server.domain.track.{TrackState, TrackSymbolsUpdated}

class TrackView @Inject() (
  eventBus: EventBus[Event],
  trackState: TrackState
) {

  eventBus.subscribe("pitch-spelling", {
    case TrackSymbolsUpdated =>
      val currentState = trackState.getTrack

      val notes = currentState.getStackedSymbolsAt[Note[MidiPitch]](Position.zero)
      val scientificPitch = NaivePitchSpelling.interpret(notes.map(_.pitch))

      val spelledNotes = notes.zip(scientificPitch).map { case (note, sp) => Note(note.duration, sp) }
      val lilyString = LilypondFormatDummy.notesToLilypond(spelledNotes)

      val lilyFile = LilypondFormatDummy.compileFile(lilyString)

      build(lilyFile)
      ()
    case _ => ()
  })

  // TODO: clean up and move to correct class, this is just for testing!
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
