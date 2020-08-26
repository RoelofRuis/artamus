package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.analysis.rna.{RNA, RNALoader}
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

  val rna = RNA(tuning, rnaRules)
  val chordInput: Array[Chord] = parseArray(tuning.parseChord).run(chords).value
  println(chords)

  val degrees = rna.nameDegrees(chordInput)

  print(tuning.printDegrees(degrees))

  def read(path: String): String = {
    val source = Source.fromFile(path)
    val res = source.getLines().mkString(" ")
    source.close()
    res
  }

}