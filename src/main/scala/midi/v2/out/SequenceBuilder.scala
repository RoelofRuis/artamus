package midi.v2.out

trait SequenceBuilder {

  def setResolution(ticksPerQuarter: Int): Unit

  def addNote(pitch: Int, start: Int, duration: Int, volume: Int): Unit

}
