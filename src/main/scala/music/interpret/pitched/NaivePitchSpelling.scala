package music.interpret.pitched

import music.symbolic.pitched._

object NaivePitchSpelling extends PitchSpelling {

  override def interpret(pitches: Seq[Pitch[PitchClass]]): Seq[Pitch[Spelled]] = {
    pitches.map(interpretOne)
  }

  private def interpretOne(pitch: Pitch[PitchClass]): Pitch[Spelled] = {
    val spelling = pitch.p.value match {
      case 0 => spell(0, 0)
      case 1 => spell(0, 1)
      case 2 => spell(1, 0)
      case 3 => spell(2, -1)
      case 4 => spell(2, 0)
      case 5 => spell(3, 0)
      case 6 => spell(3, 1)
      case 7 => spell(4, 0)
      case 8 => spell(5, -1)
      case 9 => spell(5, 0)
      case 10 => spell(6, -1)
      case 11 => spell(6, 0)
    }
    Pitch(pitch.octave, spelling)
  }

  private def spell(step: Int, acc: Int): Spelled = Spelled(Step(step), Accidental(acc))

}
