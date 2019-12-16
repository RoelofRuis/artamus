package storage.api

import server.storage.api.DbIO

trait Db {

  def newTransaction: DbTransaction with DbIO

}
