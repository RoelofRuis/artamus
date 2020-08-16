package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.Model.{Degree, ExpansionRule}

object Harmony {

  implicit class DegreeExpansion(rules: List[ExpansionRule]) {
    import nl.roelofruis.artamus.util.Ops._

    def expandByRandomRule(degrees: List[Degree]): List[Degree] = {
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

}
