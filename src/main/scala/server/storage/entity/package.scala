package server.storage

import server.storage.api.DatabaseError

package object entity {

  type EntityResult[A] = Either[EntityException, A]

  object EntityResult {
    def badData[A](ex: DatabaseError): EntityResult[A] = Left(BadData(ex))
    def notFound[A]: EntityResult[A] = Left(NotFound())
    def found[A](a: A): EntityResult[A] = Right(a)
    def ok: EntityResult[Unit] = Right(())
  }

  sealed trait EntityException extends Exception
  final case class NotFound() extends EntityException
  final case class BadData(ex: DatabaseError) extends EntityException

}
