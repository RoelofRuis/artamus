package pubsub

/* @NonThreadSafe */
class EventBus[A] extends Publisher[A] with Subscriber[A] {

  // TODO: move to more general place!

  private var subscribers: Map[String, A => Unit] = Map[String, A => Unit]()

  override def subscribe(name: String, f: A => Unit): Unit = subscribers += (name -> f)

  override def unsubscribe(name: String): Unit = subscribers -= name

  override def publish(a: A): Unit = subscribers.values.foreach(_(a))

}