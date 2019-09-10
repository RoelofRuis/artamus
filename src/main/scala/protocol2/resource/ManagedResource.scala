package protocol2.resource

import protocol2.resource.ManagedResource._

final class ManagedResource[A](res: Resource[A]) {

  private var state: State[A] = Empty()

  /**
    * Acquire an instance of this resource
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
    * After this call, acquire will throw a [[ResourceClosedException]]
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

  sealed trait ResourceException extends Exception
  final case class ResourceAcquirementException(ex: Throwable) extends ResourceException
  final case class ResourceReleaseException(ex: Throwable) extends ResourceException
  final case object ResourceClosedException extends ResourceException

  private[ManagedResource] sealed trait State[A]
  private[ManagedResource] case class Empty[A]() extends State[A]
  private[ManagedResource] case class Acquired[A](resource: A) extends State[A]
  private[ManagedResource] case class Closed[A]() extends State[A]

}
