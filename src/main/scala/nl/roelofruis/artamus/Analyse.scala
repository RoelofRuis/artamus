package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.ChordChartParsing._
import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.rendering.RenderingLoader
import nl.roelofruis.artamus.application.{Application, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.common.Temporal.{TemporalVal, Windowed}
import nl.roelofruis.artamus.core.track.Layer.{ChordLayer, ChordTimeline, RNALayer, RomanNumeralTimeline}
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.{RNAAnalysedChord, RNANode}
import nl.roelofruis.artamus.core.track.algorithms.rna.{RNAOperations, RomanNumeralAnalyser}

object Analyse extends App {

  import nl.roelofruis.artamus.application.Printer._
  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning         <- SettingsLoader.loadTuning
    rnaRules       <- RNALoader.loadRules(tuning)
    rnaAnalyser    = RomanNumeralAnalyser(tuning, rnaRules)
    (chords, _) <- readFile("src/main/resources/charts/{file}.txt")
    chordTrack     <- tuning.parseChordChart(chords)
    _              = println(printChart(chordTrack, tuning))
    degrees        = rnaAnalyser.analyse(chordTrack)
    _              = printDegrees(degrees, tuning, rnaAnalyser)
    renderer       <- RenderingLoader.loadRenderer(tuning)
    key            = degrees.headOption.map(_.get.absoluteKey).getOrElse(tuning.defaultKey)
    _              = renderer.render(makeTrack(chordTrack, degrees, tuning.defaultMetre, key))
  } yield ()

  Application.runRepeated(program)

  def makeTrack(chords: ChordTimeline, degrees: RomanNumeralTimeline, defaultMetre: Metre, key: Key): Track = {
    Track(
      TemporalVal(defaultMetre),
      TemporalVal(key),
      Seq(
        ChordLayer(chords),
        RNALayer(degrees, RNAOperations.getKeyIndicators(degrees, key)),
      )
    )
  }

  def printDegrees(degrees: RomanNumeralTimeline, tuning: Settings, analyser: RomanNumeralAnalyser): Unit = {
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
        val scoreSubZero = score.get < 0
        val warn = if (key1 == key2 && scoreSubZero) " !!UNEXPLAINED!! " else ""
        s"$warn$textChord1: $textDegree1 in $textKey1 -> $textChord2: $textDegree2 in $textKey2 [${score.get}]"
      }.foreach(println)
  }

  def printChart(chordTrack: ChordTimeline, tuning: Settings): String = {
    chordTrack
      .map { windowed => s"${tuning.printChord(windowed.element)} - ${windowed.window.duration.v}"}
      .mkString(", ")
  }

}