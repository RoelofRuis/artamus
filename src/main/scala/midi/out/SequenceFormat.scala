package midi.out

trait SequenceFormat {

  def modify(sequenceBuilder: SequenceBuilder): Unit

}
