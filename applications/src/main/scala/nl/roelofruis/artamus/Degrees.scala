package nl.roelofruis.artamus

import nl.roelofruis.artamus.analysis.rna.Model.RNANode
import nl.roelofruis.artamus.analysis.rna.RNALoader
import nl.roelofruis.artamus.parsing.Model.ParseError
import nl.roelofruis.artamus.parsing.Parser._
import nl.roelofruis.artamus.search.GraphSearch.Graph
import nl.roelofruis.artamus.tuning.Model.Tuning
import nl.roelofruis.artamus.tuning.TuningLoader

import scala.io.{Source, StdIn}
import scala.util.{Failure, Success}

object Degrees extends App {

  val result = for {
    tuning <- TuningLoader.loadTuning
    rnaAnalyser <- RNALoader.loadAnalyser(tuning)
    file = StdIn.readLine("Input file\n > ")
    chords = read(s"applications/charts/${file}.txt")
    chordParser = tuning.parser(chords)
    chordInput <- chordParser.parseList(chordParser.parseChord, " ")
    _ = println(chordInput)
    degrees = rnaAnalyser.nameDegrees(chordInput)
    _ = printDegrees(degrees, tuning)
  } yield ()

  result match {
    case Success(()) =>
    case Failure(ParseError(message, input)) => println(s"$message in [$input]")
    case Failure(ex) => throw ex
  }

  def printDegrees(option: Option[Graph[RNANode]], tuning: Tuning): Unit = {
    import nl.roelofruis.artamus.tuning.Printer._
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

  def read(path: String): String = {
    val source = Source.fromFile(path)
    val res = source.getLines().mkString(" ")
    source.close()
    res
  }

}