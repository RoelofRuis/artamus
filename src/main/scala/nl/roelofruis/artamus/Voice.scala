package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.ChordChartParsing._
import nl.roelofruis.artamus.application.Model.ParseResult
import nl.roelofruis.artamus.application.rendering.RenderingLoader
import nl.roelofruis.artamus.application.{Application, SettingsLoader}
import nl.roelofruis.artamus.core.common.Temporal.TemporalValue
import nl.roelofruis.artamus.core.track.Track.{ChordLayer, ChordTimeline, NoteLayer, NoteTimeline}
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.voicing.ChordVoicer

object Voice extends App {

  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning         <- SettingsLoader.loadTuning
    (chords, _)    <- readFile("src/main/resources/charts/{file}.txt")
    chordTrack     <- tuning.parseChordChart(chords)
    voicer         = ChordVoicer(tuning)
    voicedChords   = voicer.voiceChords(chordTrack)
    renderer       <- RenderingLoader.loadRenderer(tuning)
    _              = renderer.render(makeTrack(chordTrack, voicedChords, tuning.defaultMetre, tuning.defaultKey))
  } yield ()

  def makeTrack(chords: ChordTimeline, notes: NoteTimeline, defaultMetre: Metre, key: Key): Track = {
    Track(
      TemporalValue(defaultMetre),
      TemporalValue(key),
      Seq(
        ChordLayer(chords),
        NoteLayer(notes),
      )
    )
  }

  Application.runRepeated(program)

}