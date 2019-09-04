package protocol

import scala.reflect.ClassTag

package object client {

  trait ClientInterface {

    // TODO: Maybe move these 3 to control bus?
    def sendControl[A <: Control](message: A): Option[Boolean]

    def sendCommand[A <: Command](message: A): Option[Boolean]

    def sendQuery[A <: Query](message: A): Option[A#Res]


    // TODO: Maybe move this to initialization
    def subscribe[A <: Event: ClassTag](callback: A => A#Res): Unit

    def closeConnection(): Unit

  }

}
