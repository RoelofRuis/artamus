package client.midi.read

import client.midi.read.MidiInput.ReadAction
import javax.sound.midi.MidiMessage
import midi.MidiIO

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}

object MidiInput {

  final case class ReadAction(shouldKeep: Boolean, shouldContinue: Boolean)

}