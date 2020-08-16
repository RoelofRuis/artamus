package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextExpansionRule, TextTuning}
import nl.roelofruis.artamus.degree.Model._

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parseKey(input: String): Key = {
      val parts = input.split(' ')
      val index = tuning.noteNames.indexOf(parts(0))
      val scale = tuning.scales.find(_.name == parts(1)).get

      Key(
        PitchDescriptor(index, 0),
        Scale(scale.pitchClassSequence)
      )
    }

    def parseExpansionRules(rules: List[TextExpansionRule]): List[ExpansionRule] = {
      rules.flatMap { rule =>
        val baseDegree = parseDegrees(rule.base)
        val expansionDegrees = parseDegrees(rule.expansion)
        if (baseDegree.nonEmpty && expansionDegrees.nonEmpty) Some(ExpansionRule(baseDegree.head, expansionDegrees))
        else None
      }
    }

    def parseDegrees(input: String): List[Degree] = {
      input
        .split(' ')
        .flatMap { s => tuning.degrees.find(_.text == s) }
        .map { d => Degree(PitchDescriptor(d.pitchClass, d.step)) }
        .toList
    }
  }

}
