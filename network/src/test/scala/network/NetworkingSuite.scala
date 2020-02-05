package network

import utest.{TestSuite, Tests, test}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

object NetworkingSuite extends TestSuite with Async {

  implicit val timeout: FiniteDuration = 1 second

  override def tests: Tests = Tests {
    test("server starts and shuts down correctly") {
      val (server, _) = NetworkingStubs.newServer(9001)
      val shutdown = server.awaitShutdown()
      server.shutdown()
      shutdown.await == Success(())
    }

    test("client starts and shuts down correctly") {
      val client = NetworkingStubs.newClient(9001)
    }
  }

}
