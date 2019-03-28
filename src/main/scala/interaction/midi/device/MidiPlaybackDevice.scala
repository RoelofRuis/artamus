package interaction.midi.device

import application.domain.{Track => SymbolTrack}
import javax.inject.Inject
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (devicePool: MidiDeviceProvider) {

  private val hash = "c7797746" // TODO: load from config

  def playback(track: SymbolTrack): Unit = {
    devicePool.openOutSequencer(hash).foreach { sequencer =>
      val sequence = new Sequence(Sequence.PPQ, track.ticksPerQuarter.value.toInt)

      val midiTrack = sequence.createTrack()

      track.elements.foreach {
        case (timespan, note) =>
          midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note.pitch, note.volume), timespan.start.value))
          midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note.pitch, 0), timespan.start.value + timespan.duration.value))
        case _ =>
      }

      sequencer.setSequence(sequence)
      sequencer.setTempoInBPM(120)

      sequencer.start()
    }
  }
}
