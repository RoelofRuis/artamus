package protocol.server.impl

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException, TimeUnit}

import protocol.server.api.{ServerAPI, ServerInterface}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

// TODO: better errors and shutdown logic!
class Server(serverSocket: ServerSocket, api: ServerAPI) extends ServerInterface {

  private val connectionExecutor: ExecutorService = Executors.newFixedThreadPool(1)

  override def accept(): Unit = {
    while ( ! connectionExecutor.isShutdown) {
      ConnectionFactory.acceptNext(serverSocket, api) match {
        case Left(_: IOException) if serverSocket.isClosed =>
          // server is shut down

        case Left(ex) =>
          // unable to set up transport
          shutdown()

        case Right(connection) =>
          Try { connectionExecutor.execute(connection) } match {
            case Failure(_: RejectedExecutionException) if ! connectionExecutor.isShutdown =>
            // unable to accept connection, should not fail

            case Failure(ex) =>
              // failed executing
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
