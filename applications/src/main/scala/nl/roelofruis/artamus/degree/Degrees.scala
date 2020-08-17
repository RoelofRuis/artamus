package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Degree, Key}

import scala.io.StdIn

object Degrees extends App {

  import Harmony._
  import Parsers._
  import Printing._

  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get

  var input: List[Degree] = tuning.parseDegrees(StdIn.readLine("Input degrees separated by a space\n > "))

  var key: Key = tuning.parseKey(StdIn.readLine("Input key\n > "))

  val chords = tuning.nameChords(input, key)

  println(key)
  println(chords)
  print(tuning.printChords(chords))

}