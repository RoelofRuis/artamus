package interaction.terminal.device

import application.ports.InputDevice
import com.google.inject.Inject
import application.symbolic.Music._
import interaction.terminal.Prompt

import scala.util.Try

class TerminalInputDevice @Inject() (prompt: Prompt) extends InputDevice {

  case class InvalidTerminalInputException private (msg: String) extends Exception

  override def readData: Try[Grid] = Try {
    parseGridFromString(prompt.read("Input music data"))
  }

  private def parseGridFromString(input: String): Grid = {
    val parts = input.split("\\|")
    if (parts.length != 2) throw InvalidTerminalInputException("Input should contain only one '|'")

    val divisions = Try(parts(0).toInt).getOrElse(4)
    val elements = parts(1).trim
      .split(" ")
      .map(parseGridElementFromString)

    Grid(SubGrid(Divisions(divisions), elements), NoteLength(4))
  }

  private def parseGridElementFromString(input: String): GridElement = {
    val parts = input.split("\\*")

    if (parts.length == 1) Event(parseMidiPitch(parts(0)), PositionsOccupied(1))
    else if (parts.length == 2) {
      Event(
        parseMidiPitch(parts(0)),
        PositionsOccupied(Try(parts(1).toInt).getOrElse(1))
      )
    } else throw InvalidTerminalInputException(s"Invalid element string [$input]")
  }

  private def parseMidiPitch(input: String): Option[MidiPitch] = {
    if (input == "s") None
    else Try(input.toInt).toOption.map(MidiPitch)
  }

}
