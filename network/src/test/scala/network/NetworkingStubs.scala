package network

import network.client.api.{ClientAPI, ClientConfig, ClientFactory, ClientInterface}
import network.server.api.{ConnectionHandle, ServerAPI, ServerConfig, ServerFactory, ServerInterface}

object NetworkingStubs {
  final case class TestRequest(i: Int) { type Res = Int }
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

  class TestClientAPI() extends ClientAPI[TestEvent] {
    override def receivedEvent(event: TestEvent): Unit = ()
    override def connectingStarted(): Unit = ()
    override def connectingFailed(cause: Throwable): Unit = ()
    override def connectionEstablished(): Unit = ()
    override def connectionLost(cause: Throwable): Unit = ()
  }

  def newServer(port: Int): (ServerInterface, ServerAPI[TestRequest, TestEvent]) = {
    val api = new TestServerApi()
    val factory = new ServerFactory(ServerConfig(port), api)
    (factory.create().toOption.get, api)
  }

  def newClient(port: Int): (ClientInterface[TestRequest], ClientAPI[TestEvent]) = {
    val api = new TestClientAPI()
    val factory = new ClientFactory[TestRequest, TestEvent](ClientConfig("localhost", port), api)
    (factory.create(), api)
  }
}
