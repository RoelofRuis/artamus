package resource

import scala.util.{Failure, Success, Try}

final class Resource[A] private (acquireRes: => Either[Throwable, A], releaseRes: A => Iterable[Throwable]) {

  def acquire: Either[Throwable, A] = acquireRes

  def release(a: A): Iterable[Throwable] = releaseRes(a)

}

object Resource {

  def apply[A](acquireRes: => Either[Throwable, A], releaseRes: A => Iterable[Throwable]): Resource[A] = new Resource[A](acquireRes, releaseRes)

  /** Makes unsafe acquire and release calls safe to use as resources */
  def wrapUnsafe[A](unsafeAcquire: => A, unsafeRelease: A => Unit): Resource[A] = {
    wrapTry(Try(unsafeAcquire), a => Try(unsafeRelease(a)))
  }

  /** Wraps try calls to allow them to be used as Resource[A] */
  def wrapTry[A](tryAcquire: => Try[A], tryRelease: A => Try[Unit]): Resource[A] = {
    Resource(
      tryAcquire match {
        case Success(resource) => Right(resource)
        case Failure(ex) => Left(ex)
      },
      (a: A) => tryRelease(a) match {
        case Success(_) => Seq()
        case Failure(ex) => Seq(ex)
      }
    )
  }

}