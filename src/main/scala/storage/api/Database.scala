package storage.api

trait Database extends ModelReader {

  def newTransaction: Transaction with DbIO

}
