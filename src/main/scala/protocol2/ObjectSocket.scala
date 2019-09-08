package protocol2

trait ObjectSocket {

  def send(message: Any): Either[Iterable[Throwable], Unit]

  def receive: Either[Iterable[Throwable], Object]

}
