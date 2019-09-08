package protocol2.resource

import scala.util.{Failure, Success, Try}

class ResourceFactoryRetryDecorator[A] (
  inner: ResourceFactory[A],
  retrySleep: Int,
  failureLog: Option[Throwable => Unit] = None
) extends ResourceFactory[A] {

  def create: Try[A] = {
    inner.create match {
      case r: Success[A] => r
      case Failure(ex) =>
        failureLog.foreach(_(ex))
        Thread.sleep(retrySleep)
        create
    }
  }

  override def close(a: A): Iterable[Throwable] = inner.close(a)
}

