package server.model

import storage.api.TableModel

import scala.util.Try

trait JsonDataModel[A, I] extends TableModel[A, I] with DomainProtocol {
  import spray.json._

  implicit val format: JsonFormat[A]

  override def deserialize(data: String): Try[A] = Try {
    data.parseJson.convertTo[A]
  }
  override def serialize(obj: A): Try[String] = Try {
    obj.toJson.prettyPrint
  }
}
