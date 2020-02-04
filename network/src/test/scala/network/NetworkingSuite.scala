package network

import network.server.api.{ConnectionHandle, ServerAPI, ServerConfig, ServerFactory}
import utest.{TestSuite, Tests, test}

import scala.concurrent.duration._
import scala.language.postfixOps

object NetworkingSuite extends TestSuite with FutureTesters {

  final case class TestRequest(i: Int)
  final case class TestEvent(msg: String)

  class TestServerApi() extends ServerAPI[TestRequest, TestEvent] {
    override def serverStarted(): Unit = ()
    override def serverShuttingDown(error: Option[Throwable]): Unit = ()
    override def connectionOpened(connection: ConnectionHandle[TestEvent]): Unit = ()
    override def connectionClosed(connection: ConnectionHandle[TestEvent], error: Option[Throwable]): Unit = ()
    override def receiveFailed(connection: ConnectionHandle[TestEvent], cause: Throwable): Unit = ()
    override def afterRequest(connection: ConnectionHandle[TestEvent], response: Either[Exceptions.ResponseException, Any]): Either[Exceptions.ResponseException, Any] = response
    override def handleRequest(connection: ConnectionHandle[TestEvent], request: TestRequest): Either[Exceptions.ResponseException, Any] = Right(request)
  }

  implicit val timeout: FiniteDuration = 1 second

  val config: ServerConfig = ServerConfig(9001)
  val serverFactory = new ServerFactory(config, new TestServerApi())

  override def tests: Tests = Tests {
    val serverResult = serverFactory.create()

    assert(serverResult.isRight)
    val server = serverResult.toOption.get
    server.accept()

    test("server shuts down") {
      server.shutdown().await
    }
  }

}
