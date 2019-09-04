package server.domain.util

import scala.language.higherKinds

// TODO: see if this needs cleaning up
case class SinglePropertyMap[V[_]](map: Map[V[_], Any] = Map[V[_], Any]()) {

  def add[A](a: A)(implicit ev: V[A]): SinglePropertyMap[V] = SinglePropertyMap[V](map.updated(ev, a))

  def get[A](implicit ev: V[A]): Option[A] = map.get(ev).map(_.asInstanceOf[A])
}

case class MultiPropertyMap[V[_]](map: Map[V[_], List[Any]] = Map[V[_], List[Any]]()) {

  def add[A](a: A)(implicit ev: V[A]): MultiPropertyMap[V] = MultiPropertyMap[V](map.updated(ev, map.getOrElse(ev, List()) :+ a))

  def get[A](implicit ev: V[A]): List[A] = map.getOrElse(ev, List()).map(_.asInstanceOf[A])

}