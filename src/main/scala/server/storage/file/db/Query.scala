package server.storage.file.db

final case class Query[A, B](name: String, transform: A => Option[B])
