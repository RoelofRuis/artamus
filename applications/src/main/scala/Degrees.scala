import scala.io.StdIn

object Degrees extends App {




  def parseDegrees(): String = {
    val parts  = StdIn.readLine().split('|')

    parts.map {
      case "I" =>
      case "II" =>
      case "V" =>
      case _ =>
    }
    ""
  }

}
