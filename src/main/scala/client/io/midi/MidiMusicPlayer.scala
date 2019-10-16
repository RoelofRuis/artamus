package client.io.midi

import client.MusicPlayer
import javax.inject.Inject
import midi.out.SequenceWriter

private[midi] class MidiMusicPlayer @Inject() (
  writer: SequenceWriter
) extends MusicPlayer {

  override def play(notes: List[List[Int]]): Unit = {
    writer.writeFromFormat(QuarterNotesFormat(notes))
  }

}
