package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.{ParseError, Settings}
import nl.roelofruis.artamus.application.{ChordChartParser, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.Pitched.Chord
import nl.roelofruis.artamus.core.algorithms.GraphSearch.Graph
import nl.roelofruis.artamus.core.analysis.rna.Model.RNANode
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
    _           = printDegrees(degrees, tuning)
  } yield ()

  result match {
    case Success(()) =>
    case Failure(ParseError(message, input)) => println(s"$message in [$input]")
    case Failure(ex) => throw ex
  }

  def printDegrees(option: Option[Graph[RNANode]], tuning: Settings): Unit = {
    option match {
      case None => println("No solution found")
      case Some(graph) =>
        println(s" > Total score [${graph.score}] >")
        graph.stateList.map {
          case RNANode(chord, degree, key, weight) =>
            val textChord = tuning.printChord(chord)
            val textDegree = tuning.printDegree(degree)
            val textKey = tuning.printKey(key)
            s"$textChord: $textDegree in $textKey [$weight]"
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