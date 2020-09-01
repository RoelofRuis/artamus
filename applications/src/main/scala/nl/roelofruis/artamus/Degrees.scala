package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.{ParseError, Settings}
import nl.roelofruis.artamus.application.{ChordChartParser, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.Pitched.Chord
import nl.roelofruis.artamus.core.analysis.rna.Model.RNANode
import nl.roelofruis.artamus.core.analysis.rna.RomanNumeralAnalyser
import nl.roelofruis.artamus.core.primitives.Duration

import scala.io.{Source, StdIn}
import scala.util.{Failure, Success}

object Degrees extends App {

  import nl.roelofruis.artamus.application.Printer._

  val result = for {
    tuning      <- SettingsLoader.loadTuning
    rnaAnalyser <- RNALoader.loadAnalyser(tuning)
    file        = StdIn.readLine("Input file\n > ")
    chords      = read(s"applications/charts/${file}.txt")
    chartParser = ChordChartParser(tuning)
    chordChart  <- chartParser.parseChordChart(chords)
    _           = println(printChart(chordChart, tuning))
    degrees     = rnaAnalyser.nameDegrees(chordChart.map(_._2))
    _           = printDegrees(degrees, tuning, rnaAnalyser)
  } yield ()

  result match {
    case Success(()) =>
    case Failure(ParseError(message, input)) => println(s"$message in [$input]")
    case Failure(ex) => throw ex
  }

  def printDegrees(option: Option[Array[RNANode]], tuning: Settings, analyser: RomanNumeralAnalyser): Unit = {
    option match {
      case None => println("No solution found")
      case Some(sequence) =>
        val transitions = sequence
          .sliding(2, 1)
          .map { case Array(a, b) => (a, b, analyser.scoreTransition(a, b)) }
          .toSeq
        println(s"Total Score: ${transitions.map(_._3.get).sum}")
        transitions
          .map { case (RNANode(chord1, degree1, key1), RNANode(chord2, degree2, key2), score) =>
            val textChord1 = tuning.printChord(chord1)
            val textDegree1 = tuning.printDegree(degree1)
            val textKey1 = tuning.printKey(key1)
            val textChord2 = tuning.printChord(chord2)
            val textDegree2 = tuning.printDegree(degree2)
            val textKey2 = tuning.printKey(key2)
            s"$textChord1: $textDegree1 in $textKey1 -> $textChord2: $textDegree2 in $textKey2 [${score.get}]"
          }.foreach(println)
    }
  }

  def printChart(chart: Seq[(Duration, Chord)], tuning: Settings): String = {
    chart.map { case (duration, chord) => s"${tuning.printChord(chord)} - ${duration.v}"}.mkString(", ")
  }

  def read(path: String): String = {
    val source = Source.fromFile(path)
    val res = source.getLines().mkString(" ")
    source.close()
    res
  }

}