package storage.api

trait DataType {
  val extension: String
}

object DataType {
  case object Raw extends DataType {
    val extension: String = "dat"
  }
}
