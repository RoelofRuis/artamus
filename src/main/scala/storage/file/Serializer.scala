package storage.file

trait Serializer[A] {

  def serialize(in: A): String
  def deserialize(in: String): A

}
