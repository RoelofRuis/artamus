package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.analysis.RNA
import nl.roelofruis.artamus.degree.Model.{Chord, Key}
import nl.roelofruis.artamus.tuning.TuningLoader

import scala.io.{Source, StdIn}

object Degrees extends App {

  import nl.roelofruis.artamus.tuning.Parser._
  import nl.roelofruis.artamus.tuning.Printer._

  val tuning = TuningLoader.loadTuning
  val chords = read("applications/res/all_the_things_you_are.txt")

  val rna = RNA(tuning)
  val chordInput: Array[Chord] = parseArray(tuning.parseChord).run(chords).value
  println(chords)
  val key: Key = tuning.parseKey.run(StdIn.readLine("Input key\n > ")).value
  val degrees = rna.nameDegrees(chordInput, key)

  print(tuning.printDegrees(degrees))

  def read(path: String): String = {
    val source = Source.fromFile(path)
    val res = source.getLines().mkString(" ")
    source.close()
    res
  }

}