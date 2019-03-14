package interaction.terminal.device

import application.model.Track.TrackElements
import application.model.{Note, Ticks, TimeSpan}
import application.ports.InputDevice
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class TerminalInputDevice @Inject() (prompt: Prompt) extends InputDevice {

  case class InvalidTerminalInputException private (msg: String) extends Exception

  private final val defaultVolume = 32

  override def read(ticksPerQuarter: Int): Try[TrackElements] = {
    Try {
      val input = prompt.read("Input music data")

      parseElements(
        input.trim.split(" ").toList,
        ticksPerQuarter,
        0,
        Seq[(TimeSpan, Note)]()
      )
    }
  }

  private def parseElements(input: List[String], ticksPerQuarter: Int, pos: Long, result: Seq[(TimeSpan, Note)]): Seq[(TimeSpan, Note)] = {
    input match {
      case head :: tail =>
        val parts = head.split("\\.")
        val dur = parts(1).toInt
        val length = (ticksPerQuarter * (4.0 / dur)).toInt
        val note = parts(0)

        if (note == "s") parseElements(tail, ticksPerQuarter, pos + length, result)
        else parseElements(tail, ticksPerQuarter, pos + length, result :+ (TimeSpan(Ticks(pos), Ticks(length)), Note(note.toInt, defaultVolume)))
      case Nil => result
    }
  }
}
