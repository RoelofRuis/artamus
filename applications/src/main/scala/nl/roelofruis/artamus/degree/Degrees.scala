package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Degree, PitchDescriptor}

import scala.io.StdIn

object Degrees extends App {

  import Harmony._
  import Parsers._
  import Printing._

  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get

  var input: Array[Degree] = tuning.parseArray(StdIn.readLine("Input degrees separated by a space\n > "), tuning.parseDegree)

  var root: PitchDescriptor = tuning.parsePitchDescriptor(StdIn.readLine("Input key\n > "))

  val chords = tuning.nameChords(input, root)

  print(tuning.printChords(chords))

}