package nl.roelofruis.artamus.degree

import scala.io.StdIn

object Degrees extends App {

  val output = for {
    parser <- InputParser("applications/res/degrees.json")
    expander <- RuleExpander("applications/res/expansion-rules.json", parser)
    res <- parser.parseDegrees(StdIn.readLine("Input degrees separated by a space\n > "))
  } yield expander.expandByRandomRule(res)

  println(output)

}
