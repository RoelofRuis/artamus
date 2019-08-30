package server.handler

import protocol.QueryHandler

import scala.reflect.ClassTag

class QueryHandlerImpl extends QueryHandler {

  // TODO: implement handling of queries!!
  override def handle[A <: protocol.Query : ClassTag](query: A): A#Res = ???

}
