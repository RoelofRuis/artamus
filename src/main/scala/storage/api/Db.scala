package storage.api

trait Db {

  def newTransaction: DbTransaction with DbIO

}
