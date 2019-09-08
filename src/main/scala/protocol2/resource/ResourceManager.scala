package protocol2.resource

import protocol2.resource.ResourceManager.{Closed, ResourceClosedException}

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

class ResourceManager[A](factory: ResourceFactory[A]) {

  private var resource: Either[Closed.type, Option[A]] = Right(None)

  def get: Try[A] = {
    resource match {
      case Left(_) => Failure(ResourceClosedException("Resource manager is closed"))
      case Right(Some(r)) => Success(r)
      case Right(None) => openNew
    }
  }

  def discard: Iterable[Throwable] = {
    val result = resource match {
      case Right(Some(r)) => factory.close(r)
      case _ => List()
    }
    resource = Right(None)
    result
  }

  def close: Iterable[Throwable] = {
    val result = resource match {
      case Right(Some(r)) => factory.close(r)
      case _ => List()
    }
    resource = Left(Closed)
    result
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