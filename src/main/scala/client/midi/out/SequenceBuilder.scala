package client.midi.out

trait SequenceBuilder {

  def addNote(pitch: Int, start: Int, duration: Int, volume: Int): Unit

}
