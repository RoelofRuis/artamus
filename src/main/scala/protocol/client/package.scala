package protocol

import scala.reflect.ClassTag

package object client {

  trait ClientInterface {

    def sendControl[A <: Control](message: A): Option[Boolean]

    def sendCommand[A <: Command](message: A): Option[Boolean]

    def sendQuery[A <: Query](message: A): Option[A#Res]

    def subscribe[A <: Event: ClassTag](callback: A => A#Res): Unit

    def closeConnection(): Unit

  }

}
