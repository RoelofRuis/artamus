package nl.roelofruis.artamus.degree

import scala.io.StdIn

object Degrees extends App {

  val parser = InputParser("applications/res/degrees.json").get
  val expander = RuleExpander("applications/res/expansion-rules.json", parser).get

  var input = parser.parseDegrees(StdIn.readLine("Input degrees separated by a space\n > "))

  input = expander.expandByRandomRule(input)

  Display.prettyPrint(input)

}