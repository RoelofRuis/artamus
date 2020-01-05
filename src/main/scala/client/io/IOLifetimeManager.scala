package client.io

trait IOLifetimeManager {

  def initializeAll(): Unit

  def closeAll(): Unit

}
