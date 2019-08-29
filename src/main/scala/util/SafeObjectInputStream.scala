package util

import java.io.ObjectInputStream

import scala.util.{Failure, Success, Try}

class SafeObjectInputStream(in: ObjectInputStream, logger: Option[Logger] = None) {

  def readObject[A](): Try[A] = {
    Try(in.readObject().asInstanceOf[A])
      .transform(
        result => {
          logger.foreach(_.io("Input Stream", "IN", s"$result"))
          Success(result)
        },
        err => {
          logger.foreach(_.io("Input Stream", "IN", s"Unparsable message [$err]"))
          Failure(err)
        }
      )
  }

  def close(): Unit = in.close()
}
