package music.symbolic

import music.symbolic.containers.PropertyList
import music.symbolic.pitch.PitchClass
import music.symbolic.symbol.{Key, Note, TimeSignature}


trait Symbol[A] {
  def getProperties(a: A): PropertyList
}

object Symbol {

  implicit val noteSymbol: Symbol[Note[PitchClass]] =
    note => PropertyList.empty.add(note.duration).add(note.pitch.octave).add(note.pitch.p)

  implicit val timesignatureSymbol: Symbol[TimeSignature] =
    ts => PropertyList.empty.add(ts)

  implicit val keySymbol: Symbol[Key] =
    key => PropertyList.empty.add(key)

}

