package server.analysis

import blackboard.{Interpreter, OrderedSymbolMap, TrackSymbol}
import music.symbolic.pitch._
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.{Duration, Position}
import server.rendering.LilypondFile

class LilypondInterpreter extends Interpreter[OrderedSymbolMap[Position], LilypondFile] {

  import Properties._

  def interpret(track: OrderedSymbolMap[Position]): LilypondFile = {
    val stackedNotes: Seq[Seq[SpelledNote]] =
      track.readAllWithPosition
        .map { case (_, symbols) =>
          symbols.flatMap { symbol => spell(symbol) }
        }

    LilypondFile(
      stackedNotes,
      track.readAt(Position.zero).map(_.getProperty[TimeSignature]).head,
      track.readAt(Position.zero).map(_.getProperty[Key]).head
    )
  }

  private def spell(symbol: TrackSymbol): Option[SpelledNote] = {
    for {
      pc <- symbol.getProperty[PitchClass]
      oct <- symbol.getProperty[Octave]
      dur <- symbol.getProperty[Duration]
    } yield spellPitchClass(dur, oct, pc)
  }

  private def spellPitchClass(duration: Duration, octave: Octave, input: PitchClass): SpelledNote = {
    val (step, acc) = input.value match {
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
    SpelledNote(duration, octave, SpelledPitch(step, acc))
  }

  private def createSpelled(step: Int, acc: Int): (Step, Accidental) = (Step(step), Accidental(acc))

}
