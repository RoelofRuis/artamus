package client.io.midi

import client.MusicPlayer
import javax.inject.Inject
import midi.out.SequenceWriter
import music.symbol.Note
import music.symbol.collection.TrackSymbol

private[midi] class MidiMusicPlayer @Inject() (
  writer: SequenceWriter
) extends MusicPlayer {

  override def play(notes: Seq[TrackSymbol[Note]]): Unit = writer.writeFromFormat(NoteFormat(notes))

}
