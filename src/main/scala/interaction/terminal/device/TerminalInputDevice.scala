package interaction.terminal.device

import application.model.Unquantized.{Ticks, UnquantizedMidiNote, UnquantizedTrack}
import application.ports.InputDevice
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class TerminalInputDevice @Inject() (prompt: Prompt) extends InputDevice {

  case class InvalidTerminalInputException private (msg: String) extends Exception

  private final val TICKS_PER_QUARTER = 96

  override def readUnquantized: Try[UnquantizedTrack] = {
    Try(parseFromString(prompt.read("Input music data")))
  }

  private def parseFromString(input: String): UnquantizedTrack = {
    val elements = parseElements(
      input.trim.split(" ").toList,
      0,
      Seq[UnquantizedMidiNote]()
    )

    UnquantizedTrack(Ticks(TICKS_PER_QUARTER), elements)
  }

  private def parseElements(input: List[String], pos: Long, result: Seq[UnquantizedMidiNote]): Seq[UnquantizedMidiNote] = {
    input match {
      case head :: tail =>
        val parts = head.split("\\.")
        val dur = parts(1).toInt
        val length = (TICKS_PER_QUARTER * (4.0 / dur)).toInt
        val note = parts(0)

        if (note == "s") parseElements(tail, pos + length, result)
        else parseElements(tail, pos + length, result :+ UnquantizedMidiNote(note.toInt, Ticks(pos), Ticks(length)))
      case Nil => result
    }
  }
}
