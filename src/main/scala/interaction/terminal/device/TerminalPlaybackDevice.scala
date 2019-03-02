package interaction.terminal.device

import com.google.inject.Inject
import core.components.PlaybackDevice
import core.musicdata.MusicData
import interaction.terminal.Prompt

class TerminalPlaybackDevice @Inject() (prompt: Prompt) extends PlaybackDevice {

  override def play(data: Vector[MusicData]): Unit = {
    data.foreach( i => prompt.write(s"Playing ${i.value}"))
  }

}
