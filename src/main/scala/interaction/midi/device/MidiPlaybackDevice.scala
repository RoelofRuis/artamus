package interaction.midi.device

import application.model
import application.model.SymbolProperties.{MidiPitch, MidiVelocity, TickDuration, TickPosition}
import application.model.TrackProperties.TicksPerQuarter
import javax.inject.Inject
import javax.sound.midi._

class MidiPlaybackDevice @Inject() (devicePool: MidiDeviceProvider) {

  private val hash = "c7797746" // TODO: load from config

  def playback(track: model.Track): Unit = {
    for {
      sequencer <- devicePool.openOutSequencer(hash)
      ticksPerQuarter <- track.properties.collectFirst { case TicksPerQuarter(ticks) => ticks }
    } yield {
      val sequence = new Sequence(Sequence.PPQ, ticksPerQuarter.toInt)

      val midiTrack = sequence.createTrack()

      track.symbols.foreach { symbol =>
          for {
            pitch <- symbol.properties.collectFirst { case MidiPitch(p) => p }
            velocity <- symbol.properties.collectFirst { case MidiVelocity(v) => v }
            tickPos <- symbol.properties.collectFirst { case TickPosition(p) => p }
            tickDur <- symbol.properties.collectFirst { case TickDuration(d) => d }
          } yield {
            midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, velocity), tickPos))
            midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, pitch, 0), tickPos + tickDur))
          }
      }

      sequencer.setSequence(sequence)
      sequencer.setTempoInBPM(120)

      sequencer.start()
    }
  }
}
