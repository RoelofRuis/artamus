package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.ParseResult
import nl.roelofruis.artamus.application.{Application, ChordChartParser, FunctionalAnalysisLoader, SettingsLoader}
import nl.roelofruis.artamus.core.track.algorithms.functional.FunctionalAnalyser

object FunHarm extends App {

  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning             <- SettingsLoader.loadTuning
    functionalRules    <- FunctionalAnalysisLoader.loadRules(tuning)
    functionalAnalyser = FunctionalAnalyser(tuning, functionalRules)
    (chords, _)        <- readFile("src/main/resources/charts/{file}.txt")
    chartParser        = ChordChartParser(tuning)
    chordTrack         <- chartParser.parseChordChart(chords)
    functions          = functionalAnalyser.analyse(chordTrack)
  } yield ()

  Application.runRepeated(program)

}