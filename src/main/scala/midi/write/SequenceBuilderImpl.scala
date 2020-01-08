package midi.write

import javax.annotation.concurrent.NotThreadSafe
import javax.sound.midi.{MidiEvent, Sequence, ShortMessage}

import scala.collection.mutable

@NotThreadSafe
private[midi] class SequenceBuilderImpl private[midi] extends SequenceBuilder {

  private case class BufferedNote(pitch: Int, start: Int, duration: Int, volume: Int)

  private var resolution = 1
  private var buffer: mutable.Buffer[BufferedNote] = mutable.Buffer[BufferedNote]()

  def setResolution(res: Int): Unit = resolution = res

  def addNote(pitch: Int, start: Int, duration: Int, volume: Int): Unit = {
    buffer += BufferedNote(pitch, start, duration, volume)
  }

  def build: Sequence = {
    val sequence = new Sequence(Sequence.PPQ, resolution, 1)
    val midiTrack = sequence.createTrack()
    buffer.foreach { case x @ BufferedNote(pitch, start, duration, volume) =>
      midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, volume), start))
      midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, pitch, 0), start + duration))
    }
    sequence
  }

}
