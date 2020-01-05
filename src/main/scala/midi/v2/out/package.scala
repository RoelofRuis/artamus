package midi.v2

package object out {

  def sequenceBuilder: SequenceBuilder = new SequenceBuilderImpl

}
