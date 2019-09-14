package pubsub

/* @NonThreadSafe */
class EventBus[A] extends Publisher[A, Unit] with Subscriber[String, A, Unit] {

  private var subscribers: Map[String, A => Unit] = Map[String, A => Unit]()

  override def subscribe(name: String, f: A => Unit): Unit = subscribers += (name -> f)

  override def unsubscribe(name: String): Unit = subscribers -= name

  override def publish(a: A): Unit = subscribers.values.foreach(_(a))

  override def viewSubscriptions: Iterable[String] = subscribers.keys

}
