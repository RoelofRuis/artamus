package pubsub

/* @NonThreadSafe */
class BufferedEventBus[A] extends EventBus[A] {

  private var buffer = Seq[A]()

  override def publish(a: A): Unit = {
    if ( ! buffer.contains(a)) buffer :+= a
  }

  def flush: Int = {
    val size = buffer.size
    buffer.foreach(super.publish)
    buffer = Seq()
    size
  }

}
