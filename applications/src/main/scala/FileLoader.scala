import java.nio.file.{Files, Paths}

import spray.json._

import scala.util.Try

object FileLoader extends DefaultJsonProtocol {

  def loadList[A : JsonFormat](path: String): Try[List[A]] = load[List[A]](path)

  def load[A : JsonFormat](path: String): Try[A] = {
    Try { new String(Files.readAllBytes(Paths.get(path))).parseJson.convertTo[A] }
  }

}