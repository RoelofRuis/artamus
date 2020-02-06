package network

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}
import scala.reflect.ClassTag
import scala.util.Try

trait TestingImplicits {

  implicit class FutureOps[A](future: Future[A]) {
    def await(implicit timeout: FiniteDuration): Try[A] = Try { Await.result(future, timeout) }
  }

  implicit class EitherOps[A, B](either: Either[A, B]) {
    def isTypedLeft[X : ClassTag]: Boolean = either.left.toOption match {
      case Some(_: X) => true
      case Some(_) => false
      case None => false
    }
  }

}
