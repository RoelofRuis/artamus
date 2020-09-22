package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.ParseResult
import nl.roelofruis.artamus.application.rendering.RenderingLoader
import nl.roelofruis.artamus.application.{Application, ChordChartParser, SettingsLoader}
import nl.roelofruis.artamus.core.common.Containers.WindowedSeq
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, ChordTrack, NoteLayer, NoteTrack}
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.voicing.ChordVoicer

object Voice extends App {

  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning         <- SettingsLoader.loadTuning
    (chords, _)    <- readFile("src/main/resources/charts/{file}.txt")
    chartParser    = ChordChartParser(tuning)
    chordTrack     <- chartParser.parseChordChart(chords)
    voicer         = ChordVoicer(tuning)
    voicedChords   = voicer.voiceChords(chordTrack)
    renderer       <- RenderingLoader.loadRenderer(tuning)
    _              = renderer.render(makeTrack(chordTrack, voicedChords, tuning.defaultMetre, tuning.defaultKey))
  } yield ()

  def makeTrack(chords: ChordTrack, notes: NoteTrack, defaultMetre: Metre, defaultKey: Key): Track = {
    Track(
      WindowedSeq.startingWithInstant(defaultMetre),
      WindowedSeq.startingWithInstant(defaultKey),
      Seq(
        ChordLayer(chords),
        NoteLayer(notes),
      )
    )
  }

  Application.runRepeated(program)

}