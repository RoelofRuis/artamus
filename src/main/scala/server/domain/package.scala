package server

package object domain {

  trait DomainEvent

  case object StateChanged extends DomainEvent

}
