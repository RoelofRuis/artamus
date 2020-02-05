package network

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

trait Async {

  implicit class FutureOps[A](future: Future[A]) {
    def await(implicit timeout: FiniteDuration): Try[A] = Try { Await.result(future, timeout) }
  }

}
