package protocol.client2

import scala.language.reflectiveCalls

trait Client {

  def send[A](message: A): Option[String]

  def receive[A]: Either[String, A]

  def close: Option[Throwable]

}
