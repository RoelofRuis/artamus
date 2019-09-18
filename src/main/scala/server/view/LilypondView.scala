package server.view

import java.io._

import javax.inject.Inject
import music.interpret.pitch.NaivePitchSpelling
import music.symbolic._
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

      val stackedNotes: Seq[Seq[Note[MidiPitch]]] = currentState
        .getAllStackedSymbols[Note[MidiPitch]]
        .map { case (_, n) => n }

      val scientificPitch = stackedNotes
        .map(stack => NaivePitchSpelling.interpret(stack.map(_.pitch)).zip(stack))
        .map(_.map { case (sp, note) => Note(note.duration, sp) })

      val lilyFile = LilypondFile(
        scientificPitch,
        currentState.getSymbolAt[TimeSignature](Position.zero),
        currentState.getSymbolAt[Key](Position.zero)
      )

      build(lilyFile)
      ()
    case _ => ()
  }, active = false)

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
