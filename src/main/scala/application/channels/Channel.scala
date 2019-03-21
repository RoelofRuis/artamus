package application.channels

class Channel[T <: ChannelType] {

  private var subscribers: List[T#A => Unit] = List()

  def sub(sub: T#A => Unit): Unit = subscribers +:= sub

  def pub(obj: T#A): Unit = subscribers.foreach(_(obj))

}
