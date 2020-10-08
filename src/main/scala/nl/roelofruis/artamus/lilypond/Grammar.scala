package nl.roelofruis.artamus.lilypond

object Grammar {

  sealed trait MusicExpression
  type CompoundMusicExpression = Seq[MusicExpression]
  implicit def musicExpressionIsCompound(m: MusicExpression): CompoundMusicExpression = Seq(m)

  final case class RelativeMarker(contents: CompoundMusicExpression) extends MusicExpression

  final case class Note(
    step: Int,
    accidentals: Int,
    duration: Duration
  ) extends MusicExpression

  sealed trait Duration
  final case class EqualToPrevious() extends Duration
  final case class PowerOfTwoWithDots(power: Int, dots: Int) extends Duration

}
