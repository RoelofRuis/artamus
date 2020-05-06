package network.server.impl

import java.io.ObjectOutputStream

import network.Exceptions.WriteException

import scala.util.{Failure, Success, Try}

class SynchronizedOutputStream(outputStream: ObjectOutputStream) {

  def writeObject(obj: Any): Option[WriteException] = {
    Try {
      synchronized { outputStream.writeObject(obj) }
    } match {
      case Success(_) => None
      case Failure(ex) => Some(WriteException(ex))
    }
  }

}
