package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextDegree, TextExpansionRule}
import nl.roelofruis.artamus.degree.Model.ExpansionRule

import scala.util.{Failure, Success, Try}

final case class InputParser(degrees: List[TextDegree]) {
  def parseExpansionRules(rules: List[TextExpansionRule]): List[ExpansionRule] = {
    rules.flatMap { rule =>
      val parsedRule = for {
        baseDegree <- parseDegrees(rule.base)
        if baseDegree.nonEmpty
        expansionDegrees <- parseDegrees(rule.expansion)
      } yield ExpansionRule(baseDegree.head, expansionDegrees)

      parsedRule match {
        case Failure(ex) => println(ex); None
        case Success(res) => Some(res)
      }
    }
  }

  def parseDegrees(string: String): Try[List[TextDegree]] = Try {
    string.split(' ').flatMap { s =>
      degrees.find(_.text == s)
    }.toList
  }
}

object InputParser {

  def apply(degreesFile: String): Try[InputParser] = {
    import nl.roelofruis.artamus.degree.FileModel.Protocol._
    for {
      degrees <- FileModel.loadList[TextDegree]("applications/res/degrees.json")
    } yield InputParser(degrees)
  }

}