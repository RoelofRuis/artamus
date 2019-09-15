package server.view

import java.io._

import javax.inject.Inject
import music._
import music.interpret.NaivePitchSpelling
import music.write.LilypondFile
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

      val spelledNotes: Iterable[Note[ScientificPitch]] = notes.zip(scientificPitch).map { case (note, sp) => Note(note.duration, sp) }

      import music.write.LilypondFormat._

      //TODO: deze magic in file stoppen!
      val lilyString = spelledNotes.map(_.toLilypond).mkString(" ")
      val timeSignature = currentState.getSymbolAt[TimeSignature](Position.zero).map(_.toLilypond)
      val key = currentState.getSymbolAt[Key](Position.zero).map(_.toLilypond)

      val lilyFile = LilypondFile(lilyString, timeSignature, key)

      build(lilyFile)
      ()
    case _ => ()
  })

  def build(lilyFile: LilypondFile): Unit = {
    try {
      val file = new File("data/test.ly")

      val writer = new PrintWriter(file)
      writer.write(lilyFile.getStringContents)
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
