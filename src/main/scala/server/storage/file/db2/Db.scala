package server.storage.file.db2

trait Db {

  def newTransaction: DbTransaction with DbIO

}
