package midi.out

import midi.out.impl.{SequenceBuilder, SequenceBuilderImpl}

package object api {

  def sequenceBuilder: SequenceBuilder = new SequenceBuilderImpl

}
