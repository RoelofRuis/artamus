package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextDegree

object Model {

  final case class ExpansionRule(
    base: TextDegree,
    expansion: List[TextDegree]
  )

}
