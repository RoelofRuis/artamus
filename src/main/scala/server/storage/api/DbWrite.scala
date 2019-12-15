package server.storage.api

trait DbWrite {

  def writeKey(key: DataKey, data: String): DbResult[Unit]

  def deleteKey(key: DataKey): DbResult[Unit]

}
