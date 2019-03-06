package interaction.midi.device

import core.components.PlaybackDevice
import core.musicdata.Part
import javax.inject.Inject
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (sequencer: Sequencer) extends PlaybackDevice {

  override def play(part: Part): Unit = {
    val ticksPerQuarter = 96

    val sequence = new Sequence(Sequence.PPQ, ticksPerQuarter)

    val track = sequence.createTrack()

    val grid = part.grid

    val noteDuration = ticksPerQuarter / (grid.lengthDenominator / 4)

    grid.elements.zipWithIndex.foreach { case (musicData, i) =>
      musicData.midiNote.foreach { midiNote =>
        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, midiNote, 32), i * noteDuration))
        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, midiNote, 32), (i + 1) * noteDuration))
      }
    }

    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()
  }

}
