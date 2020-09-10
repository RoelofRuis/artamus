package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.{Application, ChordChartParser, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.Containers.Windowed
import nl.roelofruis.artamus.core.Pitched.Chord
import nl.roelofruis.artamus.core.analysis.rna.Model.{RNAAnalysedChord, RNANode}
import nl.roelofruis.artamus.core.analysis.rna.RomanNumeralAnalyser

object Analyse extends App {

  import nl.roelofruis.artamus.application.AnalysisCSVWriter._
  import nl.roelofruis.artamus.application.Printer._
  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning         <- SettingsLoader.loadTuning
    rnaRules       <- RNALoader.loadRules(tuning)
    rnaAnalyser    = RomanNumeralAnalyser(tuning, rnaRules)
    (chords, file) <- readFile("applications/charts/{file}.txt")
    chartParser    = ChordChartParser(tuning)
    chordChart     <- chartParser.parseChordChart(chords)
    _              = println(printChart(chordChart, tuning))
    degrees        = rnaAnalyser.nameDegrees(chordChart)
    _              = tuning.writeCSV(degrees, file)
    _              = printDegrees(degrees, tuning, rnaAnalyser)
  } yield ()

  Application.runRepeated(program)

  def printDegrees(option: Option[Array[RNAAnalysedChord]], tuning: Settings, analyser: RomanNumeralAnalyser): Unit = {
    option match {
      case None => println("No solution found")
      case Some(sequence) =>
        val transitions = sequence
          .sliding(2, 1)
          .map { case Array(a, b) => (a, b, analyser.scoreTransition(
            RNANode(a.chord, a.degree, a.relativeKey), RNANode(b.chord, b.degree, b.relativeKey)))
          }
          .toSeq
        println(s"Total Score: ${transitions.map(_._3.get).sum}")
        transitions
          .map { case (RNAAnalysedChord(chord1, key1, degree1, _), RNAAnalysedChord(chord2, key2, degree2, _), score) =>
            val textChord1 = tuning.printChord(chord1.element)
            val textDegree1 = tuning.printDegree(degree1)
            val textKey1 = tuning.printKey(key1)
            val textChord2 = tuning.printChord(chord2.element)
            val textDegree2 = tuning.printDegree(degree2)
            val textKey2 = tuning.printKey(key2)
            s"$textChord1: $textDegree1 in $textKey1 -> $textChord2: $textDegree2 in $textKey2 [${score.get}]"
          }.foreach(println)
    }
  }

  def printChart(chart: Seq[Windowed[Chord]], tuning: Settings): String = {
    chart.map { windowed => s"${tuning.printChord(windowed.element)} - ${windowed.window.duration.v}"}.mkString(", ")
  }

}