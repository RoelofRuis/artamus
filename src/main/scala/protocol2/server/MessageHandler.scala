package protocol2.server

trait MessageHandler {

  def handle(msg: Object): Either[Throwable, Any]

}
