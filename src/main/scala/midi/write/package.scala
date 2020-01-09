package midi

package object write {

  def sequenceBuilder: SequenceBuilder = new SequenceBuilderImpl

}
