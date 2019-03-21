package application.channels

import application.component.Logger
import application.model.Track
import javax.inject.Inject

trait ChannelType {
  type A
}

case object Playback extends ChannelType { type A = Track }

class Channel[T <: ChannelType] @Inject()(logger: Logger) {

  private var subscribers: List[T#A => Unit] = List()

  def sub(sub: T#A => Unit): Unit = {
    subscribers +:= sub
  }

  def pub(obj: T#A): Unit = {
    logger.debug(s"Channel sends [$obj] to [${subscribers.size}] subs")

    subscribers.foreach(_(obj))
  }

}
