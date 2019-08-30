package server.handler

import protocol.Query
import protocol.ServerInterface.QueryDispatcher
import server.handler.QueryDispatcherImpl.QueryMap

import scala.reflect.{ClassTag, classTag}

class QueryDispatcherImpl extends QueryDispatcher {

  private var handlers: QueryMap[QueryHandler] = new QueryMap[QueryHandler]()

  override def handle[Q <: Query : ClassTag](query: Q): Option[Q#Res] = {
    handlers
      .get[Q](query)
      .map(handler => handler.f(query))
  }

  def subscribe[A <: Query: ClassTag](h: QueryHandler[A]): Unit = {
    handlers = handlers.add[A](h)
  }

}

object QueryDispatcherImpl {

  import scala.language.higherKinds

  class QueryMap[V[_ <: Query]](inner: Map[String, Any] = Map()) {
    def add[A <: Query: ClassTag](value: V[A]): QueryMap[V] = {
      val realKey: String = classTag[A].runtimeClass.getCanonicalName
      new QueryMap(inner + ((realKey, value)))
    }

    def get[A <: Query: ClassTag](query: A): Option[V[A]] = {
      val realKey: String = query.getClass.getCanonicalName
      inner.get(realKey).map(_.asInstanceOf[V[A]])
    }
  }
}