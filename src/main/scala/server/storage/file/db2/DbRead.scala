package server.storage.file.db2

trait DbRead {

  def readKey(key: Key): DbResult[String]

}
