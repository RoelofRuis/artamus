package storage.api

object DataTypes {

  sealed trait DataType
  case object Raw extends DataType
  case object JSON extends DataType

}
