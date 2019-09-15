package pubsub

/* @NonThreadSafe */
class EventBus[A] extends Publisher[A, Unit] with Subscriber[String, A, Unit] {

  private var activeSubscribers: Map[String, A => Unit] = Map[String, A => Unit]()
  private var inactiveSubscribers: Map[String, A => Unit] = Map[String, A => Unit]()

  override def subscribe(name: String, f: A => Unit, active: Boolean = true): Unit = {
    if (active) activeSubscribers += (name -> f)
    else inactiveSubscribers += (name -> f)
  }

  override def unsubscribe(name: String): Unit = {
    activeSubscribers -= name
    inactiveSubscribers -= name
  }

  override def publish(a: A): Unit = activeSubscribers.values.foreach(_(a))

  override def viewSubscriptions: Iterable[String] = {
    activeSubscribers.keys.map(s => s"ACTIVE   [$s]") ++ inactiveSubscribers.keys.map(s => s"INACTIVE [$s]")
  }

}
