package nl.roelofruis.artamus.lilypond

object Grammar {

  final case class CompoundMusicExpression(
    expressions: Seq[MusicExpression]
  )

  sealed trait MusicExpression

  final case class Note(
    step: Int,
    accidentals: Int,
    duration: Duration
  ) extends MusicExpression

  sealed trait Duration
  final case class EqualToPrevious() extends Duration
  final case class PowerOfTwoWithDots(power: Int, dots: Int) extends Duration

}
