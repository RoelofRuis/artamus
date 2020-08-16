package nl.roelofruis.artamus

import nl.roelofruis.artamus.FileModel.Protocol._
import nl.roelofruis.artamus.FileModel.{TextDegree, TextExpansionRule}
import nl.roelofruis.artamus.Model.ExpansionRule

import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object Degrees extends App {

  final case class RuleExpander(rules: List[ExpansionRule]) {

    import Ops._

    def expandByRandomRule(degrees: List[TextDegree]): List[TextDegree] = {
      if (degrees.isEmpty) List()
      else {
        val (elem, index) = degrees.getRandomElementIndex.get
        rules.filter(_.base == elem).getRandomElement match {
          case None => degrees
          case Some(rule) => degrees.patch(index, rule.expansion, 1)
        }
      }
    }

  }

  final case class DegreeParser(degrees: List[TextDegree]) {
    def parseExpansionRules(rules: List[TextExpansionRule]): List[ExpansionRule] = {
      rules.flatMap { rule =>
        val parsedRule = for {
          baseDegree <- parse(rule.base)
          if baseDegree.nonEmpty
          expansionDegrees <- parse(rule.expansion)
        } yield ExpansionRule(baseDegree.head, expansionDegrees)

        parsedRule match {
          case Failure(ex) => println(ex); None
          case Success(res) => Some(res)
        }
      }
    }

    def parse(string: String): Try[List[TextDegree]] = Try {
      string.split(' ').flatMap { s =>
        degrees.find(_.text == s)
      }.toList
    }
  }

  val output = for {
    degrees <- FileModel.loadList[TextDegree]("applications/res/degrees.json")
    expansions <- FileModel.loadList[TextExpansionRule]("applications/res/expansion-rules.json")
    parser = DegreeParser(degrees)
    rules = parser.parseExpansionRules(expansions)
    expander = RuleExpander(rules)
    res <- parser.parse(StdIn.readLine("Input degrees separated by a space\n > "))
  } yield expander.expandByRandomRule(res)

  println(output)

}
