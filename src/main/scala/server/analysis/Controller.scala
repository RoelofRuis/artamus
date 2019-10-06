package server.analysis

import blackboard.{Interpreter, KnowledgeSource}

trait Controller[A, B] {
  val knowledgeSources: Seq[KnowledgeSource[A]]
  val interpreter: Interpreter[A, B]

  def run(state: A): B = {
    val res = knowledgeSources.foldLeft(state) {
      case (s, ks) => ks.execute(s)
    }
    interpreter.interpret(res)
  }

}
