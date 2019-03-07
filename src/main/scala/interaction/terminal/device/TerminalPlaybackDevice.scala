package interaction.terminal.device

import application.model.Music
import application.model.Music.{Event, MidiPitch, PositionsOccupied}
import application.ports.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

class TerminalPlaybackDevice @Inject() (prompt: Prompt) extends PlaybackDevice {

  override def play(grid: Music.Grid): Unit = {
    val music = grid.root.elements.map {
      case Event(None, PositionsOccupied(posOcc)) => s"[s*$posOcc]"
      case Event(Some(MidiPitch(pitch)), PositionsOccupied(posOcc)) => s"[$pitch*$posOcc]"
      case _ => ""
    }.mkString("")

    val output =
      s"""Base note length: [${grid.baseNoteLength.value}]
         |Divisions: [${grid.root.divisions.value}]
         |Music: $music
      """.stripMargin

    prompt.write(output)
  }

}
