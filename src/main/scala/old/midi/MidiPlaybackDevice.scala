package old.midi

import javax.inject.Inject
import javax.sound.midi._
import server.model
import server.model.SymbolProperties.MidiPitch

class MidiPlaybackDevice @Inject() (devicePool: MidiDeviceProvider) {

  private val hash = "c7797746" // TODO: load from config

  def playback(track: model.Track): Unit = {
    for {
      sequencer <- devicePool.openOutSequencer(hash)
    } yield {
      val sequence = new Sequence(Sequence.PPQ, 96)

      val midiTrack = sequence.createTrack()

      track.mapSymbols { symbol =>
          for {
            pitch <- symbol.properties.collectFirst { case MidiPitch(p) => p }
          } yield {
            midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 32), 0))
            midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, pitch, 0), 0))
          }
      }

      sequencer.setSequence(sequence)
      sequencer.setTempoInBPM(120)

      sequencer.start()
    }
  }
}
