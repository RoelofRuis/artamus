package object pubsub {

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new SimpleDispatcher[A]

  type Action[A <: { type Res }] = A => A#Res

}
