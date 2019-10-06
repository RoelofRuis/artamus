package blackboard

trait Interpreter[A, B] {

  def interpret(state: A): B

}
