package server.storage

import server.storage.file.db2.DatabaseError

package object neww {

  type DomainResult[A] = Either[DomainException, A]

  object DomainResult {
    def dbError[A](ex: DatabaseError): DomainResult[A] = Left(DbError(ex))
    def entityNotFound[A]: DomainResult[A] = Left(EntityNotFound())
    def success[A](a: A): DomainResult[A] = Right(a)
  }

  sealed trait DomainException extends Exception
  final case class EntityNotFound() extends DomainException
  final case class DbError(ex: DatabaseError) extends DomainException

}
