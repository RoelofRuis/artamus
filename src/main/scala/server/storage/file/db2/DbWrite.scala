package server.storage.file.db2

trait DbWrite {

  def writeKey(key: Key, data: String): DbResult[Unit]

  def deleteKey(key: Key): DbResult[Unit]

}

