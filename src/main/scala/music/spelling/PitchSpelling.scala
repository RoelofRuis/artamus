package music.spelling

import music.analysis.TwelveToneEqualTemprament
import music.collection.TrackSymbol
import music.primitives._
import music.symbols.{Chord, Note}

object PitchSpelling {

  private val tuning = TwelveToneEqualTemprament.tuning

  def spellChord(symbol: TrackSymbol[Chord]): Option[SpelledChord] = {
    for {
      root <- symbol.get[ChordRoot]
      dur <- symbol.get[Duration]
    } yield SpelledChord(dur, spellPc(root.pc))
  }

  def spellNote(symbol: TrackSymbol[Note]): Option[SpelledNote] = {
    for {
      pc <- symbol.get[PitchClass]
      oct <- symbol.get[Octave]
      dur <- symbol.get[Duration]
    } yield SpelledNote(dur, oct, spellPc(pc))
  }

  private def spellPc(pc: PitchClass): SpelledPitch = {
    // TODO: use Key information!

    pc.value match {
      case 0 => createSpelled(0, 0)
      case 1 => createSpelled(0, 1)
      case 2 => createSpelled(1, 0)
      case 3 => createSpelled(2, -1)
      case 4 => createSpelled(2, 0)
      case 5 => createSpelled(3, 0)
      case 6 => createSpelled(3, 1)
      case 7 => createSpelled(4, 0)
      case 8 => createSpelled(5, -1)
      case 9 => createSpelled(5, 0)
      case 10 => createSpelled(6, -1)
      case 11 => createSpelled(6, 0)
    }
  }

  private def createSpelled(step: Int, acc: Int): SpelledPitch = SpelledPitch(Step(step), Accidental(acc))

}
