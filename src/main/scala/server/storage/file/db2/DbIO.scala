package server.storage.file.db2

import server.storage.file.db2.DbIO.DbResult

trait DbIO {

  def read(key: Key): DbResult[String]

  def write(key: Key, data: String): DbResult[Unit]

}

object DbIO {

  type DbResult[A] = Either[DatabaseError, A]

  object DbResult {
    def success[A](a: A): DbResult[A] = Right(a)
    def done: DbResult[Unit] = Right(())
    def failure[A](error: DatabaseError): DbResult[A] = Left(error)
  }

}
