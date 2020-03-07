package storage.api

import storage.api.DataTypes.DataType

import scala.util.Try

trait TableModel[A, I] {
  val name: String
  val dataType: DataType
  def objectId(obj: A): I
  def deserialize(data: String): Try[A]
  def serialize(obj: A): Try[String]
  def serializeId(id: I): String
}

object TableModel {

  final case class ObjectId(table: String, id: String, dataType: DataType)
  final case class StorableObject(id: ObjectId, data: String)

}
