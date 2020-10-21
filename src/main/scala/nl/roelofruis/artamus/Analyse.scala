package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.ChordChartParsing._
import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.rendering.RenderingLoader
import nl.roelofruis.artamus.application.{Application, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.common.Temporal.{TemporalValue, Windowed}
import nl.roelofruis.artamus.core.track.Track.{ChordLayer, ChordTimeline, RNALayer, RomanNumeralTimeline}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, RomanNumeral}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.RNANode
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
    _              = PrettyPrinter(tuning).printDegrees(degrees, rnaAnalyser)
    renderer       <- RenderingLoader.loadRenderer(tuning)
    key            = degrees.headOption.map(_.get.absoluteKey).getOrElse(tuning.defaultKey)
    _              = renderer.render(makeTrack(chordTrack, degrees, tuning.defaultMetre, key))
  } yield ()

  Application.runRepeated(program)

  def makeTrack(chords: ChordTimeline, degrees: RomanNumeralTimeline, defaultMetre: Metre, key: Key): Track = {
    Track(
      TemporalValue(defaultMetre),
      TemporalValue(key),
      Seq(
        ChordLayer(chords),
        RNALayer(degrees, RNAOperations.getKeyIndicators(degrees, key)),
      )
    )
  }

  case class PrettyPrinter(settings: Settings) extends PitchedMaths {
    def printDegrees(degrees: RomanNumeralTimeline, analyser: RomanNumeralAnalyser): Unit = {
      val transitions = degrees
        .sliding(2, 1)
        .map { case Seq(a, b) => (a, b, analyser.scoreTransition(
          Windowed(a.window, RNANode(a.element.quality, a.element.degree, a.element.relativeKey)),
          Windowed(b.window, RNANode(b.element.quality, b.element.degree, b.element.relativeKey))
        ))}
        .toSeq
      println(s"Total Score: ${transitions.map(_._3.get).sum}")
      transitions
        .map { case (Windowed(_, RomanNumeral(quality1, key1, degree1, _)), Windowed(_, RomanNumeral(quality2, key2, degree2, _)), score) =>
          val textChord1 = settings.printChord(Chord(key1.root + degree1.root, quality1))
          val textDegree1 = settings.printDegree(degree1)
          val textKey1 = settings.printKey(key1)
          val textChord2 = settings.printChord(Chord(key2.root + degree2.root, quality2))
          val textDegree2 = settings.printDegree(degree2)
          val textKey2 = settings.printKey(key2)
          val scoreSubZero = score.get < 0
          val warn = if (key1 == key2 && scoreSubZero) " !!UNEXPLAINED!! " else ""
          s"$warn$textChord1: $textDegree1 in $textKey1 -> $textChord2: $textDegree2 in $textKey2 [${score.get}]"
        }.foreach(println)
    }
  }

  def printChart(chordTrack: ChordTimeline, tuning: Settings): String = {
    chordTrack
      .map { windowed => s"${tuning.printChord(windowed.element)} - ${windowed.window.duration.v}"}
      .mkString(", ")
  }

}