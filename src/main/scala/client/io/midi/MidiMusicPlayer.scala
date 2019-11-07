package client.io.midi

import client.MusicPlayer
import javax.inject.Inject
import midi.out.SequenceWriter
import music.playback.MidiNote

private[midi] class MidiMusicPlayer @Inject() (writer: SequenceWriter) extends MusicPlayer {

  override def play(notes: Seq[MidiNote]): Unit = writer.writeFromFormat(MidiNoteFormat(notes))

}
