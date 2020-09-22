package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.rendering.RenderingLoader
import nl.roelofruis.artamus.application.{Application, ChordChartParser, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.common.Containers.{Windowed, WindowedSeq}
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, ChordTrack, RNALayer, RomanNumeralTrack}
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.{RNAAnalysedChord, RNANode}
import nl.roelofruis.artamus.core.track.algorithms.rna.RomanNumeralAnalyser

object Analyse extends App {

  import nl.roelofruis.artamus.application.AnalysisCSVWriter._
  import nl.roelofruis.artamus.application.Printer._
  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning         <- SettingsLoader.loadTuning
    rnaRules       <- RNALoader.loadRules(tuning)
    rnaAnalyser    = RomanNumeralAnalyser(tuning, rnaRules)
    (chords, file) <- readFile("src/main/resources/charts/{file}.txt")
    chartParser    = ChordChartParser(tuning)
    chordTrack     <- chartParser.parseChordChart(chords)
    _              = println(printChart(chordTrack, tuning))
    degrees        = rnaAnalyser.analyse(chordTrack)
    _              = tuning.writeCSV(degrees, file)
    _              = printDegrees(degrees, tuning, rnaAnalyser)
    renderer       <- RenderingLoader.loadRenderer(tuning)
    _              = renderer.render(makeTrack(chordTrack, degrees, tuning.defaultMetre, tuning.defaultKey))
  } yield ()

  Application.runRepeated(program)

  def makeTrack(chords: ChordTrack, degrees: RomanNumeralTrack, defaultMetre: Metre, defaultKey: Key): Track = {
    Track(
      WindowedSeq.startingWithInstant(defaultMetre),
      WindowedSeq.startingWithInstant(defaultKey),
      Seq(
        ChordLayer(chords),
        RNALayer(degrees),
      )
    )
  }

  def printDegrees(degrees: RomanNumeralTrack, tuning: Settings, analyser: RomanNumeralAnalyser): Unit = {
    val transitions = degrees
      .sliding(2, 1)
      .map { case Seq(a, b) => (a, b, analyser.scoreTransition(
        Windowed(a.window, RNANode(a.element.chord, a.element.degree, a.element.relativeKey)),
        Windowed(b.window, RNANode(b.element.chord, b.element.degree, b.element.relativeKey))
      ))}
      .toSeq
    println(s"Total Score: ${transitions.map(_._3.get).sum}")
    transitions
      .map { case (Windowed(_, RNAAnalysedChord(chord1, key1, degree1, _)), Windowed(_, RNAAnalysedChord(chord2, key2, degree2, _)), score) =>
        val textChord1 = tuning.printChord(chord1)
        val textDegree1 = tuning.printDegree(degree1)
        val textKey1 = tuning.printKey(key1)
        val textChord2 = tuning.printChord(chord2)
        val textDegree2 = tuning.printDegree(degree2)
        val textKey2 = tuning.printKey(key2)
        s"$textChord1: $textDegree1 in $textKey1 -> $textChord2: $textDegree2 in $textKey2 [${score.get}]"
      }.foreach(println)
  }

  def printChart(chordTrack: ChordTrack, tuning: Settings): String = {
    chordTrack
      .map { windowed => s"${tuning.printChord(windowed.element)} - ${windowed.window.duration.v}"}
      .mkString(", ")
  }

}