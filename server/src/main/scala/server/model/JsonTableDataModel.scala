package server.model

import storage.api.DataModel
import storage.api.DataModel.{DataKey, JSON}

import scala.util.Try

trait JsonTableDataModel[A] extends DataModel[Map[String, A]] with DomainProtocol {
  import spray.json._

  type Shape = Map[String, A]

  val tableName: String
  implicit val format: JsonFormat[A]

  override def deserialize(data: String): Try[Map[String, A]] = Try {
    data.parseJson.convertTo[Map[String, A]]
  }
  override def serialize(obj: Map[String, A]): Try[String] = Try {
    obj.toJson.prettyPrint
  }
  override lazy val key: DataKey = DataKey(tableName, JSON)

  def empty: Shape = Map()
}