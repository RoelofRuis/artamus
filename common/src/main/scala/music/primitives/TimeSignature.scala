package music.primitives

final case class TimeSignature(
  division: TimeSignatureDivision
)

object TimeSignature {

  lazy val `4/4`: TimeSignature = TimeSignature(TimeSignatureDivision(4, 4).get)

}