package nl.roelofruis

package object pubsub {

  trait Dispatchable[+A] { val attributes: A }

  def createDispatcher[R[_] <: Dispatchable[_], A <: { type Res }](): Dispatcher[R, A] = new SimpleDispatcher[R, A]

}
