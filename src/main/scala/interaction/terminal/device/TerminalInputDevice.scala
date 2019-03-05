package interaction.terminal.device

import com.google.inject.Inject
import core.components.InputDevice
import core.musicdata.MusicGrid
import interaction.terminal.Prompt

class TerminalInputDevice @Inject() (prompt: Prompt) extends InputDevice {

  override def open: MusicGrid = {
      MusicGrid.parseFromString(prompt.read("Input music data"))
  }

}
