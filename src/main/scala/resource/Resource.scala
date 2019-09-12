package resource

import resource.Resource._

import scala.util.{Failure, Success, Try}

final class Resource[A] private (acquireRes: => Either[Throwable, A], releaseRes: A => Iterable[Throwable]) extends ResourceTransformers[A] {

  private var state: State[A] = Empty()

  /**
    * Acquire an instance of this resource.
    *
    * If there is a problem acquiring the resource, returns Left[ResourceAcquirementException]
    * If this managed resource is closed, returns Left[ResourceClosedException]
    */
  def acquire: Either[ResourceException, A] = state match {
    case Empty()    =>
      val (newState, resource) = acquireNew
      state = newState
      resource

    case Acquired(r) => Right[ResourceException, A](r)
    case Closed()   => Left(ResourceClosedException)
  }

  /**
    * Release the currently held instance of this resource
    */
  def release: Option[ResourceReleaseException] = state match {
    case Empty()    => None
    case Acquired(r) =>
      state = Empty()
      releaseInternally(r)

    case Closed()   => None
  }

  /**
    * Close this manager, and release the currently held instance of this resource.
    *
    * If [[acquire]] is called after this managed resource is closed, it will return Some([[ResourceClosedException]])
    */
  def close: Option[ResourceReleaseException] = state match {
    case Empty()    =>
      state = Closed()
      None

    case Acquired(r) =>
      state = Closed()
      releaseInternally(r)

    case Closed()   => None
  }

  def isClosed: Boolean = state == Closed()

  private def acquireNew: (State[A], Either[ResourceAcquirementException, A]) =
    acquireRes match {
      case Right(r) => (Acquired(r), Right(r))
      case Left(ex) => (Empty(), Left(ResourceAcquirementException(ex)))
    }

  private def releaseInternally(a: A): Option[ResourceReleaseException] =
    releaseRes(a) match {
      case err: Iterable[Throwable] => Some(ResourceReleaseException(err.toSeq))
      case _ => None
    }
}

object Resource {

  def apply[A](acquireRes: => Either[Throwable, A], releaseRes: A => Iterable[Throwable]): Resource[A] = new Resource[A](acquireRes, releaseRes)

  /** Makes unsafe acquire and release calls safe to use as resources */
  def wrapUnsafe[A](unsafeAcquire: => A, unsafeRelease: A => Unit): Resource[A] = {
    wrapTry(Try(unsafeAcquire), a => Try(unsafeRelease(a)))
  }

  /** Wraps try calls to allow them to be used as Resource[A] */
  def wrapTry[A](tryAcquire: Try[A], tryRelease: A => Try[Unit]): Resource[A] = {
    apply(
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

  sealed trait ResourceException extends Exception
  final case class ResourceAcquirementException(ex: Throwable) extends ResourceException
  final case class ResourceReleaseException(errors: Seq[Throwable]) extends ResourceException
  final case object ResourceClosedException extends ResourceException

  private[Resource] sealed trait State[A]
  private[Resource] case class Empty[A]() extends State[A]
  private[Resource] case class Acquired[A](resource: A) extends State[A]
  private[Resource] case class Closed[A]() extends State[A]

}

