package protocol.server

import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException}

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import resource.Resource

import scala.util.Try

class SimpleServer @Inject() (
  serverSocket: Resource[ServerSocket],
  connectionFactory: ServerConnectionFactory
) extends ServerInterface with LazyLogging {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1)

  def accept(): Unit = {
    while ( ! executor.isShutdown ) {
      val execution = for {
        server <- serverSocket.acquire.toTry
        socket <- Try { server.accept() }
        connection <- connectionFactory.connect(socket)
        execution <- acceptConnection(connection)
      } yield execution

      execution.recover {
        case ex: RejectedExecutionException =>
          if ( ! executor.isShutdown) logger.warn("Server was unable to accept new connection", ex)
        }

      if (execution.isFailure) {
        logger.error("Server exception", execution.failed.get)
        shutdown()
      }
    }
    serverSocket.close
  }

  private def acceptConnection(connection: Runnable): Try[Unit] = {
    logger.info("Accepting new connection")
    Try { executor.execute(connection) }
  }

  def shutdown(): Unit = executor.shutdown()

}
