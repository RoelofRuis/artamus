package server.domain.util

import scala.language.higherKinds

case class MultiPropertyMap[V[_]](map: Map[V[_], List[Any]] = Map[V[_], List[Any]]()) {

  def add[A](a: A)(implicit ev: V[A]): MultiPropertyMap[V] = MultiPropertyMap[V](map.updated(ev, map.getOrElse(ev, List()) :+ a))

  def set[A](a: A)(implicit ev: V[A]): MultiPropertyMap[V] = MultiPropertyMap[V](map.updated(ev, List(a)))

  def get[A](implicit ev: V[A]): List[A] = map.getOrElse(ev, List()).map(_.asInstanceOf[A])

}