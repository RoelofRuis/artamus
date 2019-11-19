package protocol.transport.server

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException, TimeUnit}

import com.typesafe.scalalogging.LazyLogging
import resource.Resource

import scala.concurrent.{Future, Promise}
import scala.util.{Success, Try}

class SimpleServer (
  serverSocket: Resource[ServerSocket],
  connectionFactory: ServerConnectionFactory
) extends LazyLogging {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1)

  private var connectionId: Long = 0

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

  def shutdown(): Future[Unit] = {
    val promise = Promise[Unit]
    new Thread (() => {
      if (! executor.isShutdown) executor.shutdown()
      executor.awaitTermination(10L, TimeUnit.SECONDS)
      if (! serverSocket.isClosed) serverSocket.close
      promise.complete(Success(()))
    }).start()
    promise.future
  }

  private def acceptConnection(connection: Runnable): Try[Unit] = {
    logger.info("Accepting new connection")
    Try { executor.execute(connection) }
  }

  private def nextConnectionId: Connection = {
    val subId = connectionId
    connectionId += 1
    Connection(subId)
  }

}

object SimpleServer {

  def apply(port: Int, serverAPI: ServerAPI): SimpleServer = {
    new SimpleServer(
      Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close()),
      new ServerConnectionFactory(serverAPI)
    )
  }

}