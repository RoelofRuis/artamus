package network.server.impl

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException, TimeUnit}

import network.Exceptions.ConnectionException
import network.server.api.{ServerAPI, ServerInterface}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

private[server] final class ServerImpl[R, E](
  serverSocket: ServerSocket,
  api: ServerAPI[R, E]
) extends Thread with ServerInterface {

  private val connectionExecutor: ExecutorService = Executors.newFixedThreadPool(1)
  private val completionPromise = Promise[Unit]()

  override def accept(): Unit = start()
  override def shutdown(): Unit = interrupt()

  override def awaitShutdown(): Future[Unit] = completionPromise.future

  override def run(): Unit = {
    api.serverStarted()
    while ( ! isInterrupted && ! serverSocket.isClosed ) {
      ConnectionFactory.acceptNext[R, E](serverSocket, api) match {
        case Left(_: ConnectionException) if serverSocket.isClosed =>
          api.serverShuttingDown()
          shutdown()

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

  override def interrupt(): Unit = {
    if (! completionPromise.isCompleted) this.synchronized {
      try {
        serverSocket.close()
        connectionExecutor.shutdown()
        connectionExecutor.awaitTermination(10, TimeUnit.SECONDS)
      } catch {
        case _: IOException =>
      } finally {
        completionPromise.complete(Success(()))
      }
    }
    super.interrupt()
  }

}
