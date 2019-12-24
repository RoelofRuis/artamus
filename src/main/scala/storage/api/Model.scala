package storage.api

import scala.util.Try

trait Model[A] {
  def key: DataKey
  def deserialize(data: String): Try[A]
  def serialize(obj: A): Try[String]
}
