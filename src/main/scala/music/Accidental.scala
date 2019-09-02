package music

final case class Accidental(value: Int) extends AnyVal

object Accidental {

  val NEUTRAL = Accidental(0)
  val FLAT = Accidental(-1)
  val SHARP = Accidental(1)

}
