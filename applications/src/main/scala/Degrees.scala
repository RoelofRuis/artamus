import spray.json._

final case class Degree(
  text: String,
  description: String
)

object Degrees extends App {

  object Protocol extends DefaultJsonProtocol {
    implicit val degreeFormat: JsonFormat[Degree] = DefaultJsonProtocol.jsonFormat2(Degree.apply)
  }

  import Protocol._

  val degrees = FileLoader.loadList[Degree]("applications/data/degrees.json")

}
