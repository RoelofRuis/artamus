package storage.api

trait DbWrite {

  def writeKey(key: DataKey, data: String): DbResult[Unit]

}
