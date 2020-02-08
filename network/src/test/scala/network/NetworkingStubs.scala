package network

import java.util.concurrent.ArrayBlockingQueue

import network.Exceptions.ResponseException
import network.client.api.{ClientAPI, ClientConfig, ClientFactory, ClientInterface}
import network.server.api._

object NetworkingStubs {
  final case class TestRequest(i: Int) { type Res = Int }
  final case class TestEvent(msg: String)

  class TestServerAPI() extends ServerAPI[TestRequest, TestEvent] {

    val requestQueue = new ArrayBlockingQueue[TestRequest => Either[ResponseException, Any]](100)

    def nextRequestHandler(h: TestRequest => Either[ResponseException, Any]): Unit = requestQueue.put(h)

    override def serverStarted(): Unit = ()
    override def serverShuttingDown(error: Option[Throwable]): Unit = ()
    override def connectionOpened(connection: ConnectionHandle[TestEvent]): Unit = ()
    override def connectionClosed(connection: ConnectionHandle[TestEvent], error: Option[Throwable]): Unit = ()
    override def receiveFailed(connection: ConnectionHandle[TestEvent], cause: Throwable): Unit = ()
    override def afterRequest(connection: ConnectionHandle[TestEvent], response: Either[ResponseException, Any]): Either[ResponseException, Any] = response
    override def handleRequest(connection: ConnectionHandle[TestEvent], request: TestRequest): Either[ResponseException, Any] = requestQueue.take()(request)
  }

  class TestClientAPI() extends ClientAPI[TestEvent] {
    override def connectionEstablished(): Unit = ()
    override def connectionLost(cause: Throwable): Unit = ()
    override def handleEvent(event: TestEvent): Unit = ()
    override def receivedInvalidEvent(cause: Throwable): Unit = ()
    override def receivedUnexpectedResponse(obj: Either[ResponseException, Any]): Unit = ()
  }

  def newClientServerPair(port: Int): (ServerInterface, TestServerAPI, ClientInterface[TestRequest], TestClientAPI) = {
    val (server, serverApi) = newServer(port)
    val (client, clientApi) = newClient(port)
    (server, serverApi, client, clientApi)
  }

  def newServer(port: Int): (ServerInterface, TestServerAPI) = {
    val api = new TestServerAPI()
    val factory = new ServerFactory(ServerConfig(port), api)
    (factory.create().toOption.get, api)
  }

  def newClient(port: Int): (ClientInterface[TestRequest], TestClientAPI) = {
    val api = new TestClientAPI()
    val factory = new ClientFactory[TestRequest, TestEvent](ClientConfig("localhost", port), api)
    (factory.create(), api)
  }
}
