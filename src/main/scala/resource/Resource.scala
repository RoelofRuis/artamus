package resource

import scala.util.{Failure, Success, Try}

trait Resource[A] {

  def acquire: Either[Throwable, A]

  def release(a: A): Option[Throwable]

}

object Resource {

  /** Makes unsafe acquire and release calls safe to use as resources */
  def wrapUnsafe[A](unsafeAcquire: => A, unsafeRelease: A => Unit): Resource[A] = new TryResource[A](Try(unsafeAcquire), Try(unsafeRelease))

  /** Wraps try calls to allow them to be used as Resource[A] */
  def wrapTry[A](tryAcquire: Try[A], tryRelease: Try[Unit]): Resource[A] = new TryResource[A](tryAcquire, tryRelease)

  private[Resource] final class TryResource[A](tryAcquire: Try[A], tryRelease: Try[Unit]) extends Resource[A] {
    def acquire: Either[Throwable, A] = tryAcquire match {
      case Success(resource) => Right(resource)
      case Failure(ex) => Left(ex)
    }
    def release(a: A): Option[Throwable] = tryRelease match {
      case Success(_) => None
      case Failure(ex) => Some(ex)
    }
  }

}