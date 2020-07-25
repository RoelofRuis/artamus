package nl.roelofruis.midi.write

import javax.annotation.concurrent.NotThreadSafe
import javax.sound.midi.Sequence

@NotThreadSafe
trait SequenceBuilder {

  def setResolution(ticksPerQuarter: Int): Unit

  def addNote(pitch: Int, start: Int, duration: Int, volume: Int): Unit

  def build: Sequence

}
