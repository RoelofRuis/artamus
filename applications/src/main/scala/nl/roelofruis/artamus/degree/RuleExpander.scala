package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextDegree, TextExpansionRule}
import nl.roelofruis.artamus.degree.Model.ExpansionRule

import scala.util.Try

final case class RuleExpander(rules: List[ExpansionRule]) {

  import nl.roelofruis.artamus.util.Ops._

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

object RuleExpander {

  def apply(ruleFile: String, parser: InputParser): Try[RuleExpander] = {
    for {
      rules <- FileModel.loadList[TextExpansionRule](ruleFile)
    } yield RuleExpander(parser.parseExpansionRules(rules))
  }

}