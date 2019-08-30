package protocol

class ClientEventRegistry {

  private var recipients = List[Event => Unit]()

  def publish(event: Event): Unit = recipients.foreach(_(event))

  def subscribe(callback: Event => Unit): Unit = recipients :+= callback

}
