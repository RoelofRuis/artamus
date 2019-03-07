package interaction.midi.device

import com.google.inject.Provider
import core.components.PlaybackDevice
import core.musicdata.{MusicData, Part}
import javax.inject.Inject
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends PlaybackDevice {

  override def play(part: Part): Unit = {
    val ticksPerQuarter = 96

    val sequence = new Sequence(Sequence.PPQ, ticksPerQuarter)

    val track = sequence.createTrack()

    val grid = part.grid

    val noteDuration = ticksPerQuarter / (grid.lengthDenominator / 4)

    buildTrack(grid.elements.toList, noteDuration).foreach(track.add)

    val sequencer = sequencerProvider.get
    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()
  }

  private def buildTrack(elements: List[MusicData], noteDuration: Int, pos: Int = 0, acc: Seq[MidiEvent] = Seq()): Seq[MidiEvent] = {
    elements match {
      case MusicData(note, dur) :: tail =>
        val notes = note match {
          case Some(midiNote) => Seq(
            new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, midiNote, 32), pos * noteDuration),
            new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, midiNote, 32), (pos + dur) * noteDuration)
          )
          case None => Seq()
        }
        buildTrack(tail, noteDuration, pos + dur, acc ++ notes)
      case Nil => acc
    }
  }
}
