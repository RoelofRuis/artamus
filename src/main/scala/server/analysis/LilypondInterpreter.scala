package server.analysis

import blackboard.{Interpreter, OrderedSymbolMap}
import music.interpret.pitched.NaivePitchSpelling
import music.symbolic.pitch.SpelledNote
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.Position
import server.rendering.LilypondFile

class LilypondInterpreter extends Interpreter[OrderedSymbolMap[Position], LilypondFile] {

  import Properties._

  def interpret(track: OrderedSymbolMap[Position]): LilypondFile = {
    val stackedNotes: Seq[Seq[SpelledNote]] =
      track.readAllWithPosition
        .map { case (_, symbols) =>
          symbols.flatMap { symbol => NaivePitchSpelling.spell(symbol) }
        }

    LilypondFile(
      stackedNotes,
      track.readAt(Position.zero).map(_.getProperty[TimeSignature]).head,
      track.readAt(Position.zero).map(_.getProperty[Key]).head
    )
  }

}
