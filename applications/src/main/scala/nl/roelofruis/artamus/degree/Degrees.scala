package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.Model.{Chord, PitchDescriptor}
import nl.roelofruis.artamus.analysis.rna.RNA
import nl.roelofruis.artamus.tuning.TuningLoader

import scala.io.{Source, StdIn}

object Degrees extends App {

  import nl.roelofruis.artamus.tuning.Parser._
  import nl.roelofruis.artamus.tuning.Printer._

  val tuning = TuningLoader.loadTuning

  val file = StdIn.readLine("Input file\n > ")

  val chords = read(s"applications/res/${file}.txt")

  val rna = RNA(tuning)
  val chordInput: Array[Chord] = parseArray(tuning.parseChord).run(chords).value
  println(chords)
  val root: PitchDescriptor = tuning.parsePitchDescriptor.run(StdIn.readLine("Input root\n > ")).value
  val degrees = rna.nameDegrees(chordInput, root)

  print(tuning.printDegrees(degrees))

  def read(path: String): String = {
    val source = Source.fromFile(path)
    val res = source.getLines().mkString(" ")
    source.close()
    res
  }

}