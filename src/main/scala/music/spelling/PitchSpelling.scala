package music.spelling

import music.collection.TrackSymbol
import music.primitives._
import music.symbols.{Chord, Key, Note}

object PitchSpelling {

  def spellChord(symbol: TrackSymbol[Chord]): Option[SpelledChord] = {
    for {
      dur <- symbol.symbol.duration
    } yield SpelledChord(dur, spellPc(symbol.symbol.root))
  }

  def spellNote(symbol: TrackSymbol[Note]): SpelledNote = {
    SpelledNote(
      symbol.symbol.duration,
      symbol.symbol.octave,
      spellPc(symbol.symbol.pitchClass)
    )
  }

  private def spellPc(pc: PitchClass, key: TrackSymbol[Key]): SpelledPitch = {
    val rootAndScale = for {
      scale <- key.get[Scale]
      root <- key.get[SpelledPitch]
    } yield (root, scale)

    val (root, scale) = rootAndScale.getOrElse(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR)

    val rootPc = tuning.spelledPitchToPc(root).value
    val scaleMap = scale
      .pcSequence
      .zipWithIndex
      .map { case (pc, step) => tuning.pc(pc + rootPc) -> tuning.step(step) }
      .toMap

    // geeft je de step (opzoeken in key)
    scaleMap.get(pc)

    // else de dichtstbijzijnde

    ???
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
