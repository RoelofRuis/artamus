package network.client

package object api {

  trait ClientAPI[E] extends ClientEventHandler[E] with ClientCallbacks

}
