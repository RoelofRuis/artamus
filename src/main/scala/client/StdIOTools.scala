package client

object StdIOTools {

  def readString(message: String): String = {
    read[String](message, "Not a valid string", scala.io.StdIn.readLine())
  }

  def readInt(message: String): Int = {
    read[Int](message, "Not a valid int", scala.io.StdIn.readInt())
  }

  def read[A](message: String, failureMessage: String, readFunc: => A): A = {
    println(message)
    try { readFunc }
    catch { case _: Exception =>
      println(failureMessage)
      read[A](message, failureMessage, readFunc)
    }
  }

}
