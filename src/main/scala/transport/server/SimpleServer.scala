package transport.server

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException, TimeUnit}

import com.typesafe.scalalogging.LazyLogging
import resource.Resource

import scala.util.Try

class SimpleServer (
  serverSocket: Resource[ServerSocket],
  connectionFactory: ServerConnectionFactory
) extends LazyLogging {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1)

  private var connectionId: Long = 0
  private val SERVER_SUB_KEY = "server-out"

  def accept(): Unit = {
    while ( ! executor.isShutdown ) {
      val execution = for {
        server <- serverSocket.acquire.toTry
        socket <- Try { server.accept() }
        connection <- connectionFactory.connect(socket, nextConnectionId)
        execution <- acceptConnection(connection)
      } yield execution

      val recovered = execution.recover {
        case ex: RejectedExecutionException if ! executor.isShutdown =>
          logger.warn("Server was unable to accept new connection", ex)

        case _: IOException if serverSocket.isClosed =>
          logger.info("Server was shut down")
        }

      if (recovered.isFailure) {
        logger.error("Server exception, forcing shutdown", recovered.failed.get)
        shutdown()
      }
    }
  }

  def shutdown(): Unit = {
    if (! executor.isShutdown) executor.shutdown()
    executor.awaitTermination(10L, TimeUnit.SECONDS)
    if (! serverSocket.isClosed) serverSocket.close
  }

  private def acceptConnection(connection: Runnable): Try[Unit] = {
    logger.info("Accepting new connection")
    Try { executor.execute(connection) }
  }

  private def nextConnectionId: String = {
    val subId = s"${SERVER_SUB_KEY}_$connectionId"
    connectionId+=1
    subId
  }

}

object SimpleServer {

  def apply(port: Int, bindings: ServerBindings): SimpleServer = {
    new SimpleServer(
      Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close()),
      new ServerConnectionFactory(bindings)
    )
  }

}