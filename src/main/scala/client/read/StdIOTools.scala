package client.read

object StdIOTools {

  def readInt(message: String): Int = {
    println(message)
    try { scala.io.StdIn.readInt() }
    catch { case _: NumberFormatException =>
      println("Not a valid int")
      readInt(message)
    }
  }

}
