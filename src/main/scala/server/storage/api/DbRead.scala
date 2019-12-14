package server.storage.api

trait DbRead {

  def readKey(key: DataKey): DbResult[String]

}
