package server.analysis.blackboard

class Controller[A](val knowledgeSources: Seq[KnowledgeSource[A]]) {

  def run(state: A): A = {
    knowledgeSources.foldLeft(state) {
      case (s, ks) =>
        val newState = ks.execute(s)
        newState
    }
  }

}
