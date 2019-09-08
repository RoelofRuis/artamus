package protocol2.resource

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

class ResourceManager[A](factory: ResourceFactory[A]) {

  private var resource: Option[A] = None

  def get: Try[A] = {
    resource match {
      case Some(r) => Success(r)
      case None =>
        factory.create match {
          case Success(r) =>
            resource = Some(r)
            Success(r)

          case ex: Failure[_] => ex
        }
    }
  }

  def close(): Iterable[Throwable] = {
    if (resource.isDefined) {
      val closeResult = factory.close(resource.get)
      resource = None
      closeResult
    }
    else List()
  }

}
