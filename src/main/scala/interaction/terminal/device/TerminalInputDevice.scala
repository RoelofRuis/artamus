package interaction.terminal.device

import com.google.inject.Inject
import core.components.InputDevice
import core.musicdata.MusicData
import interaction.terminal.Prompt

class TerminalInputDevice @Inject() (prompt: Prompt) extends InputDevice {

  override def open: Stream[MusicData] = {
      prompt.read("Input music data")
        .trim
        .split(" ")
        .flatMap(MusicData.parseFromString).toStream
  }

}
