package nl.roelofruis.artamus.application.rendering

import nl.roelofruis.artamus.application.File
import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.lilypond.Model.LilypondSettings
import nl.roelofruis.artamus.core.track.Pitched.Quality
import spray.json.{DefaultJsonProtocol, JsonFormat}

object RenderingLoader {
  import nl.roelofruis.artamus.application.rendering.RenderingLoader.FileModel.{TextLilypondSettings, TextQualitySpelling}

  def loadRenderer(tuning: Settings): ParseResult[LilypondRenderer] = {
    def parseQualitySpelling(settings: TextLilypondSettings): Map[Quality, String] = {
      settings.qualitySpelling.flatMap { case TextQualitySpelling(symbol, spelling) =>
        tuning.qualityMap.get(symbol).map { quality => (quality, spelling) }
      }.toMap
    }

    for {
      textSettings    <- File.load[TextLilypondSettings]("src/main/resources/data/lilypond_settings.json")
      qualitySpelling = parseQualitySpelling(textSettings)
    } yield {
      val settings = LilypondSettings(
        textSettings.pngResolution,
        textSettings.lilypondVersion,
        textSettings.paperSize,
        textSettings.pitchClassSequence,
        textSettings.numPitchClasses,
        textSettings.stepNames,
        textSettings.flatSpelling,
        textSettings.sharpSpelling,
        textSettings.dotSpelling,
        qualitySpelling,
        textSettings.quarterTempo
      )

      LilypondRenderer(settings)
    }
  }

  private object FileModel extends DefaultJsonProtocol {

    final case class TextQualitySpelling(
      symbol: String,
      spelling: String
    )

    object TextQualitySpelling {
      implicit val qualitySpellingFormat: JsonFormat[TextQualitySpelling] = jsonFormat2(TextQualitySpelling.apply)
    }

    final case class TextLilypondSettings(
      pngResolution: Int,
      lilypondVersion: String,
      paperSize: String,
      pitchClassSequence: List[Int],
      numPitchClasses: Int,
      stepNames: List[String],
      flatSpelling: String,
      sharpSpelling: String,
      dotSpelling: String,
      qualitySpelling: List[TextQualitySpelling],
      quarterTempo: Int,
    )

    object TextLilypondSettings {
      implicit val settingsFormat: JsonFormat[TextLilypondSettings] = jsonFormat11(TextLilypondSettings.apply)
    }

  }

}
