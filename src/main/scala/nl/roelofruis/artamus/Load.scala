package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.ParseResult
import nl.roelofruis.artamus.application.rendering.RenderingLoader
import nl.roelofruis.artamus.application.{Application, SettingsLoader}
import nl.roelofruis.artamus.core.common.Temporal.TemporalVal
import nl.roelofruis.artamus.core.track.Layer.{NoteLayer, NoteSeq}
import nl.roelofruis.artamus.core.track.Pitched.Key
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.lilypond.{LilypondConverter, Parser}

import scala.util.Try

object Load extends App {

  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning     <- SettingsLoader.loadTuning
    (piece, _) <- readFile("src/main/resources/melody/{file}.ly")
    expr       <- Try { Parser.parseLilypond(piece).get.value }
    converter  = LilypondConverter(tuning)
    noteTrack  <- converter.convert(expr).toTry
    renderer   <- RenderingLoader.loadRenderer(tuning)
    _          = renderer.render(makeTrack(noteTrack, tuning.defaultMetre, tuning.defaultKey))
  } yield ()

  def makeTrack(notes: NoteSeq, defaultMetre: Metre, key: Key): Track = {
    Track(
      TemporalVal(defaultMetre),
      TemporalVal(key),
      Seq(
        NoteLayer(notes),
      )
    )
  }

  Application.runRepeated(program)


}