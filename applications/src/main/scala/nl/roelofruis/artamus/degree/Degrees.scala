package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.Degree

import scala.io.StdIn

object Degrees extends App {

  val parser = InputParser("applications/res/degrees.json").get
  val expander = RuleExpander("applications/res/expansion-rules.json", parser).get
  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get

  println(tuning)

  var input: List[Degree] = parser.parseDegrees(StdIn.readLine("Input degrees separated by a space\n > "))

  input = expander.expandByRandomRule(input)

}