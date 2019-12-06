package music.domain.track

import music.math.temporal.{Position, Window}
import music.primitives.Chord

import scala.collection.BufferedIterator
import scala.collection.immutable.SortedMap

final case class Chords private(
  chords: SortedMap[Position, (Window, Chord)]
) {

  def writeChord(window: Window, chord: Chord): Chords = {
    copy(chords.updated(window.start, (window, chord)))
  }

  def mapChords(f: Chord => Chord): Chords = {
    copy(
      chords = chords.map { case (position, (window, chord)) => (position, (window, f(chord))) }
    )
  }

  def read: BufferedIterator[(Window, Chord)] = chords.valuesIterator.buffered

}

object Chords {

  def apply(): Chords = new Chords(SortedMap())

}