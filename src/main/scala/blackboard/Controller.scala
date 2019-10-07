package blackboard

import com.typesafe.scalalogging.LazyLogging

class Controller[A](val knowledgeSources: Seq[KnowledgeSource[A]]) extends LazyLogging {

  def run(state: A): A = {
    knowledgeSources.foldLeft(state) {
      case (s, ks) =>
        val newState = ks.execute(s)
        logger.debug("New state {}", newState)
        newState
    }
  }

}
