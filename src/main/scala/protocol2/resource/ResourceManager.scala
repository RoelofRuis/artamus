package protocol2.resource

import protocol2.resource.ResourceManager.{Closed, ResourceClosedException}

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

class ResourceManager[A](
  factory: ResourceFactory[A],
  reopenable: Boolean
) {

  private var resource: Either[Closed.type, Option[A]] = Right(None)

  def get: Try[A] = {
    resource match {
      case Left(_) =>
        if ( ! reopenable) Failure(ResourceClosedException("Resource is not reopenable"))
        else openNew
      case Right(Some(r)) => Success(r)
      case Right(None) => openNew
    }
  }

  def close(): Iterable[Throwable] = {
    resource match {
      case Right(Some(r)) =>
        val closeResult = factory.close(r)
        resource = Left(Closed)
        closeResult

      case _ => List()
    }
  }

  def isClosed: Boolean = resource match {
    case Left(_) => true
    case _ => false
  }

  private def openNew: Try[A] = {
    factory.create match {
      case Success(r) =>
        resource = Right(Some(r))
        Success(r)

      case ex: Failure[_] => ex
    }
  }

}

object ResourceManager {

  private case object Closed

  case class ResourceClosedException(msg: String) extends Exception

}