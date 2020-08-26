package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.analysis.rna.Analyser.RNANode
import nl.roelofruis.artamus.analysis.rna.{Analyser, RNALoader}
import nl.roelofruis.artamus.degree.Model.Chord
import nl.roelofruis.artamus.tuning.TuningLoader

import scala.io.{Source, StdIn}

object Degrees extends App {

  import nl.roelofruis.artamus.tuning.Parser._
  import nl.roelofruis.artamus.tuning.Printer._

  val tuning = TuningLoader.loadTuning
  val rnaRules = RNALoader.loadRNA(tuning)

  val file = StdIn.readLine("Input file\n > ")

  val chords = read(s"applications/charts/${file}.txt")

  val rna = Analyser(tuning, rnaRules)
  val chordInput: Array[Chord] = parseArray(tuning.parseChord).run(chords).value
  println(chords)

  val graphs = rna.nameDegrees(chordInput)

  graphs.foreach { graph =>
    println(s" > Total score [${graph.score}] >")
    graph.stateList.map {
      case RNANode(chord, degree, key, weight) =>
        val textChord = tuning.printChord(chord)
        val textDegree = tuning.printDegree(degree)
        val textKey = tuning.printKey(key)
        s"$textChord: $textDegree in $textKey [$weight]"
    }.foreach(println)
  }

  def read(path: String): String = {
    val source = Source.fromFile(path)
    val res = source.getLines().mkString(" ")
    source.close()
    res
  }

}