package client

import client.midi.MidiDeviceDescription
import javax.sound.midi._

object MidiClient extends App {

  private val devices = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

  // RECORDING BASICS
  val recordingDevice = devices
    .collectFirst { case descr: MidiDeviceDescription if descr.hash == "e98b95f2" => descr.info }
    .map(MidiSystem.getMidiDevice)
    .get

  recordingDevice.open()

  val sequencer: Sequencer = MidiSystem.getSequencer(false)

  val transmitter = recordingDevice.getTransmitter

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
  recordingDevice.close()

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

  // TRANSMITTING BASICS
  val transmittingDevice = devices
    .collectFirst { case descr: MidiDeviceDescription if descr.hash == "c7797746" => descr.info }
    .map(MidiSystem.getMidiDevice)
    .get

  transmittingDevice.open()

  val sequencer2: Sequencer = MidiSystem.getSequencer(false)

  val receiver = transmittingDevice.getReceiver

  sequencer.getTransmitter.setReceiver(receiver)

  sequencer.open()

  val sequence2 = new Sequence(Sequence.PPQ, 96)

  val midiTrack = sequence.createTrack()

  midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 32), 0))
  midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0), 0))

  sequencer.setSequence(sequence)
  sequencer.setTempoInBPM(120)

  sequencer.start()

  Thread.sleep(5000)

  sequencer.stop()
  sequencer.close()
  receiver.close()
  transmittingDevice.close()

}
