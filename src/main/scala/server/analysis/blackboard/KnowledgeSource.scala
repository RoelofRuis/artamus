package server.analysis.blackboard

trait KnowledgeSource[A] {

  def execute(state: A): A

}
