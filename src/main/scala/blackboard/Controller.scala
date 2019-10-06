package blackboard

import com.typesafe.scalalogging.LazyLogging

class Controller[A, B](
  val knowledgeSources: Seq[KnowledgeSource[A]],
  val interpreter: Interpreter[A, B]
) extends LazyLogging {

  def run(state: A): B = {
    val res = knowledgeSources.foldLeft(state) {
      case (s, ks) =>
        val newState = ks.execute(s)
        logger.debug("New state {}", newState)
        newState
    }
    interpreter.interpret(res)
  }

}
