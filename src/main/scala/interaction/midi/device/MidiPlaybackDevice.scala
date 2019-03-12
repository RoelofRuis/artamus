package interaction.midi.device

import application.model.Note
import application.model.{Track => SymbolTrack}
import application.ports.PlaybackDevice
import javax.inject.{Inject, Provider}
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends PlaybackDevice {

  override def playback(track: SymbolTrack[Note]): Unit = {
    val sequence = new Sequence(Sequence.PPQ, track.ticksPerQuarter.value.toInt)

    val midiTrack = sequence.createTrack()

    track.elements.foreach {
      case (timespan, note) =>
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note.pitch, note.volume), timespan.start.value))
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note.pitch, 0), timespan.start.value + timespan.duration.value))
      case _ =>
    }

    val sequencer = sequencerProvider.get
    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()
  }
}
