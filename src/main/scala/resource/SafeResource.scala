package resource

import scala.util.{Failure, Success, Try}

final class SafeResource[A](unsafeAcquire: => A, unsafeRelease: A => Unit) extends Resource[A] {

  def acquire: Either[Throwable, A] = Try(unsafeAcquire) match {
    case Success(resource) => Right(resource)
    case Failure(ex) => Left(ex)
  }

  def release(a: A): Option[Throwable] = Try {unsafeRelease(a)} match {
    case Success(_) => None
    case Failure(ex) => Some(ex)
  }

}
