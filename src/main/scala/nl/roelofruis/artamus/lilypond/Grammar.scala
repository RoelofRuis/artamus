package nl.roelofruis.artamus.lilypond

object Grammar {

  type LilypondDocument = Seq[TLE]

  /** TopLevelExpression */
  sealed trait TLE

  final case class Comment(
    message: String
  ) extends TLE with ME

  /** MusicExpression */
  sealed trait ME extends TLE
  /** CompoundMusicExpression */
  final case class CME(contents: Seq[ME]) extends TLE

  implicit def musicExpressionIsCompound(m: ME): CME = CME(Seq(m))

  final case class Relative(
    to: Pitch,
    contents: CME
  ) extends ME

  final case class Note(
    pitch: Pitch,
    duration: Duration = EqualToPrevious(),
    tie: Boolean = false
  ) extends ME

  final case class Rest(
    duration: Duration = EqualToPrevious()
  ) extends ME

  final case class BarLineCheck() extends ME

  final case class Pitch(
    step: Int,
    accidentals: Int,
    octave: Int
  )

  sealed trait Duration
  final case class EqualToPrevious() extends Duration
  final case class PowerOfTwoWithDots(power: Int, dots: Int) extends Duration

}
