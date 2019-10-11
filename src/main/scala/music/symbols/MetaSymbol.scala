package music.symbols

import music.collection.SymbolProperties
import music.primitives.{Key, TimeSignature}

case object MetaSymbol extends SymbolType {

  def timeSignature(ts: TimeSignature): SymbolProperties[MetaSymbol.type] =
    SymbolProperties[MetaSymbol.type].add(ts)

  def key(key: Key): SymbolProperties[MetaSymbol.type] =
    SymbolProperties[MetaSymbol.type].add(key)

}