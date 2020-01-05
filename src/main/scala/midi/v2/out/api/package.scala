package midi.v2.out

import midi.v2.out.impl.{SequenceBuilder, SequenceBuilderImpl}

package object api {

  def sequenceBuilder: SequenceBuilder = new SequenceBuilderImpl

}
