package server.analysis

import blackboard.{Interpreter, KnowledgeSource, OrderedSymbolMap}
import music.symbolic.temporal.Position
import server.rendering.LilypondFile

class RenderingController extends Controller[OrderedSymbolMap[Position], LilypondFile] {
  override val knowledgeSources: Seq[KnowledgeSource[OrderedSymbolMap[Position]]] = Seq(
    new ChordAnalyser(),
    new PitchHistogramAnalyser()
  )

  override val interpreter: Interpreter[OrderedSymbolMap[Position], LilypondFile] = new LilypondInterpreter
}
