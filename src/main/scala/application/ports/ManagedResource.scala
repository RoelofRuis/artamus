package application.ports

/**
  * System resources that are provided by pluggable components can use this interface to provide their closable
  * resources to the core.
  *
  * The `close()` method will be called by the application on shutdown.
  */
trait ManagedResource {

  def getName: String

  def getDescription: String

  def close(): Unit

}
