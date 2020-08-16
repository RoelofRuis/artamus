package nl.roelofruis.artamus

import nl.roelofruis.artamus.FileModel.TextDegree

object Model {

  final case class ExpansionRule(
    base: TextDegree,
    expansion: List[TextDegree]
  )

}
