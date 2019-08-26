package interaction.socket

import java.io.PrintStream
import java.net.{ServerSocket, SocketException}

import application.api.{CommandBus, Driver, EventBus}
import javax.inject.Inject

import scala.io.BufferedSource

class SocketDriver @Inject() () extends Driver {

  val server = new ServerSocket(9999)

  def run(bus: CommandBus, eventBus: EventBus): Unit = {
    try {
      while (true) {
        val s = server.accept()
        val in = new BufferedSource(s.getInputStream).getLines()
        val out = new PrintStream(s.getOutputStream)

        out.println(in.next())
        out.flush()
        s.close()
      }
    } catch  {
      case _: SocketException =>
    }
  }

  override def close(): Unit = {
    server.close()
  }

}
