package network.server.api

import scala.concurrent.Future

trait ServerInterface {

  def accept(): Unit

  def shutdown(): Unit

  def awaitShutdown(): Future[Unit]

}
