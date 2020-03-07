package storage.api

object DataTypes {

  // TODO: this might be moved to server?
  sealed trait DataType
  case object Raw extends DataType
  case object JSON extends DataType

}
