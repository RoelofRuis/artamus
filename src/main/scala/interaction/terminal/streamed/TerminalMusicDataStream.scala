package interaction.terminal.streamed

import com.google.inject.Inject
import core.components.MusicDataStream
import core.musicdata.MusicData
import interaction.terminal.Prompt

class TerminalMusicDataStream @Inject() (prompt: Prompt) extends MusicDataStream {

  override def open: Stream[MusicData] = {
      prompt.read("Input music data")
        .split(",")
        .map(_.trim)
        .map(s => MusicData(s)).toStream
  }

}
