package protocol2

trait ObjectSocket {

  def send[A](message: A): Either[Iterable[Throwable], Unit]

  def receive[A]: Either[Iterable[Throwable], A]

  def close: Iterable[Throwable]

}
