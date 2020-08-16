package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextDegree, TextExpansionRule, TextScale, TextTuning}
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, Key}

import scala.io.StdIn

object Degrees extends App {

  import Parsers._
  import Harmony._

  val degrees = FileModel.loadList[TextDegree]("applications/res/degrees.json").get
  val expansionRules = FileModel.loadList[TextExpansionRule]("applications/res/expansion-rules.json")
    .map(degrees.parseExpansionRules)
    .get
  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get
  val scales = FileModel.loadList[TextScale]("applications/res/scales.json").get

  var input: List[Degree] = degrees.parseDegrees(StdIn.readLine("Input degrees separated by a space\n > "))

  input = expansionRules.expandByRandomRule(input)

  def nameChords(degrees: List[Degree], key: Key): List[Chord] = ???

}