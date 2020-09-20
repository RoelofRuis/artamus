package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.ParseResult
import nl.roelofruis.artamus.application.{Application, ChordChartParser, SettingsLoader}

object Voice extends App {

  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning         <- SettingsLoader.loadTuning
    (chords, _)    <- readFile("applications/charts/{file}.txt")
    chartParser    = ChordChartParser(tuning)
    chordTrack     <- chartParser.parseChordChart(chords)
  } yield ()

  Application.runRepeated(program)

}