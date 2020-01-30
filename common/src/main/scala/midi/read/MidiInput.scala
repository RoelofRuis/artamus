package midi.read

import javax.sound.midi.MidiMessage
import midi.MidiIO
import midi.read.MidiInput.ReadAction

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}

object MidiInput {

  final case class ReadAction(shouldKeep: Boolean, shouldContinue: Boolean)

}