package application.channels

class Channel[A] private () {

  private var subscribers: List[A => Unit] = List()

  def sub(sub: A => Unit): Unit = subscribers +:= sub

  def pub(obj: A): Unit = subscribers.foreach(_(obj))

}

private[application] object Channel {

  def apply[A]: Channel[A] = new Channel[A]()

}