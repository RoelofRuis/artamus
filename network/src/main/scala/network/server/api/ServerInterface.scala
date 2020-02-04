package network.server.api

import scala.concurrent.Future

trait ServerInterface {

  def accept(): Unit

  def shutdown(): Future[Unit]

}
