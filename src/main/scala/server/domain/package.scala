package server

import blackboard.OrderedSymbolMap
import music.symbolic.temporal.Position

package object domain {

  trait DomainEvent

  case class StateChanged(track: OrderedSymbolMap[Position]) extends DomainEvent

}
