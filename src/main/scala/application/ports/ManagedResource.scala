package application.ports

trait ManagedResource {

  def getName: String

  def close(): Unit

}
