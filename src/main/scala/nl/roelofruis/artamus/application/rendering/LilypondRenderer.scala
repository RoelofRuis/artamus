package nl.roelofruis.artamus.application.rendering

import java.io.{File, PrintWriter}

import nl.roelofruis.artamus.application.rendering.Model.LilypondSettings
import nl.roelofruis.artamus.core.layout.DisplayableMusic
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.lilypond.{Document, LilypondFormatting}

case class LilypondRenderer(settings: LilypondSettings) extends LilypondFormatting {

  def render(track: Track): Unit = {
    // to displayable music
    val displayableMusic = DisplayableMusic.fromTrack(track)

    // to lilypond file
    val document = format(displayableMusic)
    val contents = Document.write(document)

    // write to file
    val sourceFile = new File(s"src/main/resources/rendering/lily.ly")
    val writer = new PrintWriter(sourceFile)
    writer.write(contents)
    writer.close()

    // invoke lilypond
    import sys.process._

    val result = s"""lilypond -fpng --output="src/main/resources/rendering" -dresolution=${settings.pngResolution} "${sourceFile.getAbsolutePath}"""".!!

    println(result)
  }

}
