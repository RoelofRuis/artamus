package object pubsub {

  trait RequestContainer[+A] { val attributes: A }

  def createDispatcher[R[_] <: RequestContainer[_], A <: { type Res }](): Dispatcher[R, A] = new SimpleDispatcher[R, A]

}
