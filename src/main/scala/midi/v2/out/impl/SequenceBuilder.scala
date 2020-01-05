package midi.v2.out.impl

import javax.sound.midi.Sequence

trait SequenceBuilder {

  def setResolution(ticksPerQuarter: Int): Unit

  def addNote(pitch: Int, start: Int, duration: Int, volume: Int): Unit

  def build: Sequence

}
