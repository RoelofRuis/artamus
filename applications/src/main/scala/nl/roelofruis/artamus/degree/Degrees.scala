package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextExpansionRule, TextTuning}
import nl.roelofruis.artamus.degree.Model.{Degree, Key}

import scala.io.StdIn

object Degrees extends App {

  import Harmony._
  import Parsers._

  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get
  val expansionRules = FileModel.loadList[TextExpansionRule]("applications/res/expansion-rules.json")
    .map(tuning.parseExpansionRules)
    .get

  var input: List[Degree] = tuning.parseDegrees(StdIn.readLine("Input degrees separated by a space\n > "))

  input = expansionRules.expandByRandomRule(input)

  var key: Key = tuning.parseKey(StdIn.readLine("Input key\n > "))

  Display.prettyPrint(input)
  println(key)

}