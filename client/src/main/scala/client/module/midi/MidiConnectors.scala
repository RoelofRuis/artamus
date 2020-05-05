package client.module.midi

import javax.sound.midi.{Receiver, Transmitter}
import client.patching.CanConnect

import scala.util.Try

object MidiConnectors {

  implicit def canConnectMidi[R <: Receiver]: CanConnect[Transmitter, R] = (t: Transmitter, r: R) => Try {
    t.setReceiver(r)
  }

}
