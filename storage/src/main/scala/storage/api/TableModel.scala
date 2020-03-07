package storage.api

import storage.api.DataTypes.DataType

import scala.util.Try

trait TableModel[A, I] {
  val name: String
  val dataType: DataType
  def objectId(obj: A): I // TODO: See if objectId and serializeId always occur together!
  def deserialize(data: String): Try[A]
  def serialize(obj: A): Try[String]
  def serializeId(id: I): String
}

object TableModel {

  final case class ObjectId(table: String, id: String, dataType: DataType) // TODO: Does the data type have to be in here?
  final case class StorableObject(id: ObjectId, data: String)

}
