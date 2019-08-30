package client

import client.midi.MidiDeviceDescription
import javax.sound.midi._

object MidiClient extends App {

  private val devices = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

  val device = devices
    .collectFirst { case descr: MidiDeviceDescription if descr.hash == "e98b95f2" => descr.info }
    .map(MidiSystem.getMidiDevice)
    .get

  device.open()

  val sequencer: Sequencer = MidiSystem.getSequencer(false)

  val transmitter = device.getTransmitter

  transmitter.setReceiver(sequencer.getReceiver)

  sequencer.open()

  val sequence = new Sequence(Sequence.PPQ, 96, 1)

  sequencer.setSequence(sequence)
  sequencer.setTempoInBPM(120)
  sequencer.recordEnable(sequence.getTracks()(0), -1)
  sequencer.setTickPosition(0)
  sequencer.startRecording()

  Thread.sleep(5000)

  sequencer.stop()
  sequencer.close()
  transmitter.close()
  device.close()


  val track: Track = sequence.getTracks()(0)

  Range(0, track.size).foreach { i =>
    val midiEvent = track.get(i)
    val midiMessage = midiEvent.getMessage
    println(midiMessage)
    midiMessage match {
      case msg: ShortMessage => println(s"${msg.getCommand} - ${msg.getChannel} - ${msg.getData1} - ${msg.getData2}")
      case _ =>
    }
  }

}
