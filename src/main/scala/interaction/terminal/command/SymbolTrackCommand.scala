package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.ToSymbolTrack
import application.domain._

import scala.util.Try

class SymbolTrackCommand extends Command {

  val name = "symb"
  val helpText = "Transform a track to a symbolic representation"
  override val argsHelp = Some("[id: Int]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[Track](args(0).toLong))
    } yield {
      bus.execute(ToSymbolTrack(id))
        .fold(
          ex => display(s"Unable to transform to symbol track [$ex]"),
          symbolTrack => display(symbolTrack.toString)
        )
    }

    returnRecovered(res)
  }

  private def printSymbolTrack(symbolTrack: SymbolTrack): String = {
    val spacesPerQuarter = 12

    val notes: Array[(Int, (Int, Int))] = symbolTrack.symbols.map { symbol =>
      for {
        spacePos <- symbol.properties.collectFirst { case Position(pos, nv) => (nv.toDouble * pos * spacesPerQuarter).toInt }
        spaceDur <- symbol.properties.collectFirst { case Duration(len, nv) => (nv.toDouble * len * spacesPerQuarter).toInt }
        midiPitch <- symbol.properties.collectFirst { case MidiPitch(pitch) => pitch }
      } yield {
        spacePos -> (spaceDur, midiPitch)
      }
    }.collect {
      case Some(res) => res
    }.toArray.sortBy { case (pos, _) => pos }

    notes.foreach { case (pos, (dur, pitch)) => println(s"$pos: $dur, $pitch") }

    asCharList(0, 0, (spacesPerQuarter * 4) * 2, notes)
  }

  private def asCharList(pos: Int, activeLen: Int, max: Int, notes: Array[(Int, (Int, Int))]): String = {
    if (pos > max) ""
    else if (notes.headOption.exists { case (notePos, (_, _)) => notePos == pos }) {
      val noteString = notes.head._2._2.toString
      val noteStringLen = noteString.length
      val noteDur = notes.head._2._1

      noteString + asCharList(pos + noteStringLen, noteDur - noteStringLen, max, notes.tail)
    } else if (activeLen > 0) {
      "-" + asCharList(pos + 1, activeLen - 1, max, notes)
    } else {
      "." + asCharList(pos + 1, 0, max, notes)
    }
  }


}
