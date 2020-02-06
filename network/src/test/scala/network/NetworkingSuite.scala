package network

import network.Exceptions.{ConnectionException, Unauthenticated}
import network.NetworkingStubs.TestRequest
import utest._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

object NetworkingSuite extends TestSuite with TestingImplicits {

  implicit val timeout: FiniteDuration = 1 second

  override def tests: Tests = Tests {
    test("server starts and shuts down correctly") {
      val (server, _) = NetworkingStubs.newServer(9001)
      val shutdown = server.awaitShutdown()
      server.shutdown()
      shutdown.await ==> Success(())
    }
    test("client fails when sending request without server") {
      val (client, _) = NetworkingStubs.newClient(9002)
      val res = client.send(TestRequest(42))
      assert(res.isTypedLeft[ConnectionException])
    }
    test("client receives response from server") {
      val (server, serverApi) = NetworkingStubs.newServer(9003)
      val (client, _) = NetworkingStubs.newClient(9003)
      server.accept()
      serverApi.nextRequestHandler(_ => Left(Unauthenticated))
      assert(client.send(TestRequest(42)).isTypedLeft[Unauthenticated.type])
    }
  }

}
