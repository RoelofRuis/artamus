package storage.api

import storage.api.Model.DataKey

import scala.util.Try

trait Model[A] {
  val key: DataKey
  def deserialize(data: String): Try[A]
  def serialize(obj: A): Try[String]
}

object Model {

  final case class DataKey(name: String, dataType: DataType)

  sealed trait DataType
  case object Raw extends DataType
  case object JSON extends DataType

}