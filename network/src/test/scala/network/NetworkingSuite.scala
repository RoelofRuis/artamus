package network

import network.Exceptions.{ConnectionException, LogicError, ReadException}
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
    test("client receives left response from server") {
      val (server, serverApi, client, _) = NetworkingStubs.newClientServerPair(9003)
      server.accept()
      serverApi.nextRequestHandler(_ => Left(LogicError))

      assert(client.send(TestRequest(42)).isTypedLeft[LogicError.type])
    }
    test("client receives right response from server") {
      val (server, serverApi, client, _) = NetworkingStubs.newClientServerPair(9004)
      server.accept()
      serverApi.nextRequestHandler(_ => Right(84))

      client.send(TestRequest(42)) ==> Right(84)
    }
    test("server hangs up during request") {
      val (server, serverApi, client, _) = NetworkingStubs.newClientServerPair(9005)
      server.accept()
      serverApi.nextRequestHandler(_ => {
        wait(1000)
        Right(1)
      })
      server.shutdown()
      val res = client.send(TestRequest(42))

      assert(res.isTypedLeft[ConnectionException])
    }
    test("server hangs up after request") {
      val (server, serverApi, client, _) = NetworkingStubs.newClientServerPair(9005)
      server.accept()
      serverApi.nextRequestHandler(_ => {
        wait(1000)
        Right(1)
      })
      val res = client.send(TestRequest(42))
      server.shutdown()

      assert(res.isTypedLeft[ReadException])
    }
  }

}
