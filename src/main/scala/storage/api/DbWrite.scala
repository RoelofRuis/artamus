package storage.api

import server.storage.api.{DataKey, DbResult}

trait DbWrite {

  def writeKey(key: DataKey, data: String): DbResult[Unit]

}
