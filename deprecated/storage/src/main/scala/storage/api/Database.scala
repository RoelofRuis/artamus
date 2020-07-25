package storage.api

trait Database extends DbReader {

  def newTransaction: Transaction with DbIO

}
