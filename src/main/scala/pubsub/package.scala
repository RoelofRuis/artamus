package object pubsub {

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new SimpleDispatcher[A]

}
