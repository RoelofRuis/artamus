package protocol2

trait ObjectSocket {

  def send[A](message: A): Option[String]

  def receive[A]: Either[String, A]

  def close: Option[Throwable]

}
