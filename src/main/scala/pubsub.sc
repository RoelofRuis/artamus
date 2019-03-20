

case class MyState(name: String)

class PublisherNetwork {

  val myStatePub = new StatePublisher[MyState]()

  def subscribe[A](sub: A => Unit): Unit = sub match {
    case s: (MyState => Unit) => myStatePub.subscribe(s)
    case _ => loggeR("Shit is fokt!")
  }

  def publish[A](obj: A): Unit = obj match {
    case s: MyState => myStatePub.publish(s)
    case _ => logger("shit is fokt!")
  }

}


val pub = new StatePublisher[MyState]

pub.subscribe(println)

pub.publish(MyState("Micronesia"))