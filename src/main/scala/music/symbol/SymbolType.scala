package music.symbol

import music.primitives.Duration

trait SymbolType {

  def getDuration: Duration = Duration.zero

}
