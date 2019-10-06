package server.analysis

import blackboard.Interpreter
import music.symbolic.pitch._
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.{Duration, Position}
import server.domain.track.container.{SymbolTrack, SymbolProperties}
import server.rendering.LilypondFile

class LilypondInterpreter extends Interpreter[SymbolTrack[Position], LilypondFile] {

  def interpret(track: SymbolTrack[Position]): LilypondFile = {
    val stackedNotes: Seq[Seq[SpelledNote]] =
      track.readAllWithPosition
        .map { case (_, symbols) =>
          symbols.flatMap { symbol => spell(symbol.props) }
        }

    LilypondFile(
      stackedNotes,
      track.readAt(Position.zero).map(_.props.get[TimeSignature]).head,
      track.readAt(Position.zero).map(_.props.get[Key]).head
    )
  }

  private def spell(symbol: SymbolProperties): Option[SpelledNote] = {
    for {
      pc <- symbol.get[PitchClass]
      oct <- symbol.get[Octave]
      dur <- symbol.get[Duration]
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
