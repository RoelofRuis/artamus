package nl.roelofruis.artamus.lilypond

object Grammar {

  sealed trait MusicExpression
  type CompoundMusicExpression = Seq[MusicExpression]
  implicit def musicExpressionIsCompound(m: MusicExpression): CompoundMusicExpression = Seq(m)

  final case class Relative(
    to: Pitch,
    contents: CompoundMusicExpression
  ) extends MusicExpression

  final case class Note(
    pitch: Pitch,
    duration: Duration
  ) extends MusicExpression

  final case class Rest(
    duration: Duration
  ) extends MusicExpression

  final case class Pitch(
    step: Int,
    accidentals: Int,
    octave: Int
  )

  sealed trait Duration
  final case class EqualToPrevious() extends Duration
  final case class PowerOfTwoWithDots(power: Int, dots: Int) extends Duration

}
