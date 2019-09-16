package transport

trait Connection {

  def send(message: Any): Either[Seq[Throwable], Unit]

  def receive: Either[Seq[Throwable], Object]

  def isClosed: Boolean

  def close: Seq[Throwable]

}
