package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextDegree, TextExpansionRule}
import nl.roelofruis.artamus.degree.Model.{Degree, ExpansionRule}

object Parsers {

  implicit class DegreesParseOps(degrees: List[TextDegree]) {
    def parseExpansionRules(rules: List[TextExpansionRule]): List[ExpansionRule] = {
      rules.flatMap { rule =>
        val baseDegree = parseDegrees(rule.base)
        val expansionDegrees = parseDegrees(rule.expansion)
        if (baseDegree.nonEmpty && expansionDegrees.nonEmpty) Some(ExpansionRule(baseDegree.head, expansionDegrees))
        else None
      }
    }

    def parseDegrees(string: String): List[Degree] = {
      string
        .split(' ')
        .flatMap { s => degrees.find(_.text == s) }
        .map { d => Degree(d.tuningDescriptor.pitchClass, d.tuningDescriptor.step) }
        .toList
    }
  }



}
