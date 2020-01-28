package client

trait ModuleLifetimeHooks {

  def initializeAll(): Unit

  def closeAll(): Unit

}
