package storage.api

trait DbRead {

  def readKey(key: DataKey): DbResult[String]

}
