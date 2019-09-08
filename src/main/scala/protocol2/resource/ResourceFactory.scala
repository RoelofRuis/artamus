package protocol2.resource

import scala.util.Try

trait ResourceFactory[A] {

  def create: Try[A]

  def close(a: A): Iterable[Throwable]

}
