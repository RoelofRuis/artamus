package protocol

import java.io.ObjectInputStream

import scala.util.Try

class ServerInputStream(in: ObjectInputStream) {

  def readObject[A](): Try[A] = {
    Try(in.readObject().asInstanceOf[A])
  }

  def readRequestMessage: Try[ServerRequestMessage] = {
    readObject[ServerRequestMessage]()
  }

  def close(): Unit = in.close() // TODO: maybe move this out of here

}
