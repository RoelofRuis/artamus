package server.model

import storage.api.DataTypes.JSON
import storage.api.{DataTypes, DataModel}

import scala.util.Try

trait JsonDataModel[A, I] extends DataModel[A, I] with DomainProtocol {
  import spray.json._

  override val dataType: DataTypes.DataType = JSON

  implicit val format: JsonFormat[A]

  override def deserialize(data: String): Try[A] = Try {
    data.parseJson.convertTo[A]
  }
  override def serialize(obj: A): Try[String] = Try {
    obj.toJson.prettyPrint
  }
}
