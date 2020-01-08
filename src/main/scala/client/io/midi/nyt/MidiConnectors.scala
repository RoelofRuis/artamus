package client.io.midi.nyt

import javax.sound.midi.{Receiver, Transmitter}
import patchpanel.CanConnect

import scala.util.Try

object MidiConnectors {

  implicit def canConnectMidi[R <: Receiver]: CanConnect[Transmitter, R] = (t: Transmitter, r: R) => Try {
    t.setReceiver(r)
  }

}
