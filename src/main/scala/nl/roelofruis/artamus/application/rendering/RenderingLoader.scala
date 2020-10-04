package nl.roelofruis.artamus.application.rendering

import nl.roelofruis.artamus.application.File
import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.core.track.Pitched.{Quality, QualityGroup}
import nl.roelofruis.artamus.lilypond.LilypondSettings
import spray.json.{DefaultJsonProtocol, JsonFormat}

object RenderingLoader {
  import nl.roelofruis.artamus.application.rendering.RenderingLoader.FileModel.{TextLilypondSettings, TextQualitySpelling, TextQualityGroupSpelling}

  def loadRenderer(tuning: Settings): ParseResult[LilypondRenderer] = {
    def parseQualitySpelling(settings: TextLilypondSettings): Map[Quality, String] = {
      settings.qualitySpelling.flatMap { case TextQualitySpelling(symbol, spelling) =>
        tuning.qualityMap.get(symbol).map { quality => (quality, spelling) }
      }.toMap
    }

    def parseQualityGroupSpelling(settings: TextLilypondSettings): Map[QualityGroup, String] = {
      settings.qualityGroupSpelling.flatMap { case TextQualityGroupSpelling(symbol, spelling) =>
        tuning.qualityGroupMap.get(symbol).map { quality => (quality, spelling) }
      }.toMap
    }

    for {
      textSettings    <- File.load[TextLilypondSettings]("src/main/resources/data/lilypond_settings.json")
      qualitySpelling = parseQualitySpelling(textSettings)
      qualityGroupSpelling = parseQualityGroupSpelling(textSettings)
    } yield {
      val settings = LilypondSettings(
        textSettings.pngResolution,
        textSettings.lilypondVersion,
        textSettings.paperSize,
        textSettings.pitchClassSequence,
        textSettings.numPitchClasses,
        textSettings.stepNames,
        textSettings.degreeNames,
        textSettings.flatSpelling,
        textSettings.sharpSpelling,
        textSettings.dotSpelling,
        qualitySpelling,
        qualityGroupSpelling,
        tuning.scaleMap.map(_.swap),
        textSettings.quarterTempo
      )

      LilypondRenderer(settings)
    }
  }

  private object FileModel extends DefaultJsonProtocol {

    final case class TextQualityGroupSpelling(
      symbol: String,
      degreeNotation: String,
    )

    object TextQualityGroupSpelling {
      implicit val qualityGroupSpellingFormat: JsonFormat[TextQualityGroupSpelling] = jsonFormat2(TextQualityGroupSpelling.apply)
    }

    final case class TextQualitySpelling(
      symbol: String,
      chordNotation: String,
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
      degreeNames: List[String],
      flatSpelling: String,
      sharpSpelling: String,
      dotSpelling: String,
      qualitySpelling: List[TextQualitySpelling],
      qualityGroupSpelling: List[TextQualityGroupSpelling],
      quarterTempo: Int,
    )

    object TextLilypondSettings {
      implicit val settingsFormat: JsonFormat[TextLilypondSettings] = jsonFormat13(TextLilypondSettings.apply)
    }

  }

}
