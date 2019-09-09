package music.containers

import music.Position
import music.properties.Symbols.Symbol

trait CanInspectSymbols {

  /** Whether there exists at least one symbol of type A at the given position. */
  def hasSymbolAt[A](pos: Position)(implicit ev: Symbol[A]): Boolean

}
