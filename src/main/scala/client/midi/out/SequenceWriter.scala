package client.midi.out

trait SequenceWriter {

  def writeFromFormat(format: SequenceFormat): Unit

}
