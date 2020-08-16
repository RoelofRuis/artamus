package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.{TextDegree, TextExpansionRule, TextScale, TextTuning}
import nl.roelofruis.artamus.degree.Model.{Degree, ExpansionRule, Key, PitchSpelling, Scale}

object Parsers {

  implicit class TuningParseOps(tuning: TextTuning) {
    def parseKey(input: String): Key = {
      val parts = input.split(' ')
      val index = tuning.noteNames.indexOf(parts(0))
      val scale = tuning.scales.find(_.name == parts(1)).get

      Key(
        PitchSpelling(index, 0),
        Scale(scale.pitchClassSequence)
      )
    }
  }

  implicit class DegreesParseOps(degrees: List[TextDegree]) {
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
        .flatMap { s => degrees.find(_.text == s) }
        .map { d => Degree(d.tuningDescriptor.pitchClass, d.tuningDescriptor.step) }
        .toList
    }
  }



}
