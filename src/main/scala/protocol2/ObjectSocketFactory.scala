package protocol2

trait ObjectSocketFactory {

  def connect(): Either[String, ObjectSocketConnection]

}
