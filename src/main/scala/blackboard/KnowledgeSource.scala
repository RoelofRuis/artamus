package blackboard

trait KnowledgeSource[A] {

  def canExecute(state: A): Boolean
  def execute(state: A): A

}
