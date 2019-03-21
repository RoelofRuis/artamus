package application.component

import application.channels.{Channel, Logging}
import com.google.inject.Inject

private[application] class Logger @Inject() (channel: Channel[Logging.type]) {

  def debug(text: String): Unit = channel.pub(s"[DEBUG] $text")

}
