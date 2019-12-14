package server.storage.file.db

@deprecated
final case class Query[A, B](name: String, transform: A => Option[B])
