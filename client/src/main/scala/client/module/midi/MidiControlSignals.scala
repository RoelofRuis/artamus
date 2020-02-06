package client.module.midi

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import javax.sound.midi.{MidiMessage, Receiver, ShortMessage}

class MidiControlSignals @Inject() () extends Receiver with LazyLogging {

  override def send(message: MidiMessage, microsecondPosition: Long): Unit = {
    message match {
      case s: ShortMessage if s.getCommand == ShortMessage.NOTE_ON =>
        println(s"NOTE_ON    ch=${s.getChannel} note=${s.getData1} vel=${s.getData2}")
      case s: ShortMessage if s.getCommand == ShortMessage.NOTE_OFF =>
        println(s"NOTE_OFF   ch=${s.getChannel} note=${s.getData1} vel=${s.getData2}")
      case s: ShortMessage if s.getCommand == ShortMessage.CONTROL_CHANGE =>
        println(s"CONTROL    ch=${s.getChannel} code=${s.getData1} val=${s.getData2}")
      case s: ShortMessage if s.getCommand == ShortMessage.POLY_PRESSURE =>
        println(s"AFTERTOUCH ch=${s.getChannel} note=${s.getData1} val=${s.getData2}")
      case e => println(s"$microsecondPosition: [$e]")
    }
  }

  override def close(): Unit = ()
}
