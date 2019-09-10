package resource

import scala.util.{Failure, Success, Try}

// TODO: try to make monadic?
trait Resource[A] {

  def acquire: Either[Throwable, A]

  def release(a: A): Option[Throwable]

}

object Resource {

  /** Makes unsafe acquire and release calls safe to use as resources */
  def safe[A](unsafeAcquire: => A, unsafeRelease: A => Unit): Resource[A] = new SafeResource[A](unsafeAcquire, unsafeRelease)

  private[Resource] final class SafeResource[A](unsafeAcquire: => A, unsafeRelease: A => Unit) extends Resource[A] {
    def acquire: Either[Throwable, A] = Try(unsafeAcquire) match {
      case Success(resource) => Right(resource)
      case Failure(ex) => Left(ex)
    }

    def release(a: A): Option[Throwable] = Try {unsafeRelease(a)} match {
      case Success(_) => None
      case Failure(ex) => Some(ex)
    }
  }

}