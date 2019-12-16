package storage.api

import server.storage.api.{DataKey, DbResult}

trait DbRead {

  def readKey(key: DataKey): DbResult[String]

}
