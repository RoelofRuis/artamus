package protocol.server.impl

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException, TimeUnit}

import protocol.server.api.{ServerAPI, ServerInterface}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

private[server] final class Server[E](serverSocket: ServerSocket, api: ServerAPI[E]) extends ServerInterface {

  private val connectionExecutor: ExecutorService = Executors.newFixedThreadPool(1)

  override def accept(): Unit = {
    api.serverStarted()
    while ( ! connectionExecutor.isShutdown) {
      ConnectionFactory.acceptNext[E](serverSocket, api) match {
        case Left(_: IOException) if serverSocket.isClosed =>
          api.serverShuttingDown()

        case Left(ex) =>
          api.serverShuttingDown(Some(ex))
          shutdown()

        case Right(connection) =>
          Try { connectionExecutor.execute(connection) } match {
            case Failure(_: RejectedExecutionException) if ! connectionExecutor.isShutdown =>
              // unable to accept connection, server can continue

            case Failure(ex) =>
              api.serverShuttingDown(Some(ex))
              shutdown()

            case Success(_) =>
              // was accepted
          }
      }
    }
    Right(())
  }

  def shutdown(): Future[Unit] = {
    val promise = Promise[Unit]
    new Thread (() => {
      if ( ! connectionExecutor.isShutdown) connectionExecutor.shutdown()
      connectionExecutor.awaitTermination(10L, TimeUnit.SECONDS)
      if ( ! serverSocket.isClosed) serverSocket.close()
      promise.complete(Success(()))
    }).start()
    promise.future
  }

}
