package interaction.midi.device

import application.ports.PlaybackDevice
import com.google.inject.Provider
import application.model.Music.{Event, Grid, GridElement}
import javax.inject.Inject
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (sequencerProvider: Provider[Sequencer]) extends PlaybackDevice {

  override def play(grid: Grid): Unit = {
    val ticksPerQuarter = 96

    val sequence = new Sequence(Sequence.PPQ, ticksPerQuarter)

    val track = sequence.createTrack()

    val subgrid = grid.root

    val noteDuration = ticksPerQuarter / (subgrid.divisions.value / 4)

    buildTrack(subgrid.elements.toList, noteDuration).foreach(track.add)

    val sequencer = sequencerProvider.get
    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()
  }

  private def buildTrack(elements: List[GridElement], noteDuration: Int, pos: Int = 0, acc: Seq[MidiEvent] = Seq()): Seq[MidiEvent] = {
    elements match {
      case Event(note, dur) :: tail =>
        val notes = note match {
          case Some(midiNote) => Seq(
            new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, midiNote.value, 32), pos * noteDuration),
            new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, midiNote.value, 32), (pos + dur.value) * noteDuration)
          )
          case None => Seq()
        }
        buildTrack(tail, noteDuration, pos + dur.value, acc ++ notes)
      case _ => acc
    }
  }
}
