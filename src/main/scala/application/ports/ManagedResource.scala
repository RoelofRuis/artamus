package application.ports

trait ManagedResource {

  def getName: String

  def getDescription: String

  def close(): Unit

}
