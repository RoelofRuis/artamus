package nl.roelofruis.artamus.degree

object Model {

  final case class ExpansionRule(
    base: Degree,
    expansion: List[Degree]
  )

  final case class Degree(
    pitchClass: Int,
    step: Int
  )

}
