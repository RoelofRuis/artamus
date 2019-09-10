package resource

import resource.ManagedResource._

final class ManagedResource[A] private (res: Resource[A]) {

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
      closeAcquired(r)

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
      closeAcquired(r)

    case Closed()   => None
  }

  def isClosed: Boolean = state == Closed()

  private def acquireNew: (State[A], Either[ResourceAcquirementException, A]) =
    res.acquire match {
      case Right(r) => (Acquired(r), Right(r))
      case Left(ex) => (Empty(), Left(ResourceAcquirementException(ex)))
    }

  private def closeAcquired(a: A): Option[ResourceReleaseException] = res.release(a).map(ResourceReleaseException)

}

object ManagedResource {

  def managed[A](implicit rec: Resource[A]): ManagedResource[A] = new ManagedResource[A](rec)

  sealed trait ResourceException extends Exception
  final case class ResourceAcquirementException(ex: Throwable) extends ResourceException
  final case class ResourceReleaseException(ex: Throwable) extends ResourceException
  final case object ResourceClosedException extends ResourceException

  private[ManagedResource] sealed trait State[A]
  private[ManagedResource] case class Empty[A]() extends State[A]
  private[ManagedResource] case class Acquired[A](resource: A) extends State[A]
  private[ManagedResource] case class Closed[A]() extends State[A]

}
