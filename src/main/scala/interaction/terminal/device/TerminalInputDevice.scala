package interaction.terminal.device

import com.google.inject.Inject
import core.components.InputDevice
import core.musicdata.MusicData
import interaction.terminal.Prompt

class TerminalInputDevice @Inject() (prompt: Prompt) extends InputDevice {

  override def open: Stream[MusicData] = {
      prompt.read("Input music data")
        .split(",")
        .map(_.trim)
        .map(s => MusicData(s.toInt)).toStream
  }

}
