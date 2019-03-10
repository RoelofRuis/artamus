package interaction.midi.device

import application.model.Unquantized.{UnquantizedMidiNote, UnquantizedTrack}
import application.ports.PlaybackDevice
import javax.inject.{Inject, Provider}
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends PlaybackDevice {

  override def playbackUnquantized(track: UnquantizedTrack): Unit = {
    val sequence = new Sequence(Sequence.PPQ, track.ticksPerQuarter.value.toInt)

    val midiTrack = sequence.createTrack()

    track.elements.foreach {
      case UnquantizedMidiNote(midiPitch, start, duration) =>
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, midiPitch, 32), start.value))
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, midiPitch, 0), start.value + duration.value))
      case _ =>
    }

    val sequencer = sequencerProvider.get
    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()
  }
}
