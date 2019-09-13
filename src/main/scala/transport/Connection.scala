package transport

trait Connection {

  def send(message: Any): Either[Iterable[Throwable], Unit]

  def receive: Either[Iterable[Throwable], Object]

  def isClosed: Boolean

  def close: List[Throwable]

}
