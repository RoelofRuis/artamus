package application.component

import application.channels.LoggedMessage
import com.google.inject.Inject

private[application] class Logger @Inject() (eventBus: DomainEventBus) {

  def debug(text: String): Unit = eventBus.publish(LoggedMessage(s"[DEBUG] $text"))

}
