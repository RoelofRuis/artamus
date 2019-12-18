package client.io.midi

import client.MusicPlayer
import javax.inject.Inject
import midi.out.SequenceWriter
import music.model.perform.TrackPerformance

private[midi] class MidiMusicPlayer @Inject() (writer: SequenceWriter) extends MusicPlayer {

  override def play(track: TrackPerformance): Unit = writer.writeFromFormat(MidiNoteFormat(track.notes.toSeq))

}
