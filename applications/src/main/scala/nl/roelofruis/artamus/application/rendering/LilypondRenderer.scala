package nl.roelofruis.artamus.application.rendering

import java.io.{File, PrintWriter}

import nl.roelofruis.artamus.application.rendering.Model.LilypondSettings
import nl.roelofruis.artamus.core.layout.DisplayableMusic
import nl.roelofruis.artamus.core.track.Track
import nl.roelofruis.artamus.lilypond.LilypondFormatting

case class LilypondRenderer(settings: LilypondSettings) extends LilypondFormatting {

  def render(track: Track): Unit = {
    // to displayable music
    val displayableMusic = DisplayableMusic.fromTrack(track)

    // to lilypond file
    val contents = format(displayableMusic)

    // write to file
    val sourceFile = new File(s"applications/rendering/lily.ly")
    val writer = new PrintWriter(sourceFile)
    writer.write(contents)
    writer.close()

    // invoke lilypond
    import sys.process._

    val result = s"""lilypond -fpng --output="applications/rendering" -dresolution=${settings.pngResolution} "${sourceFile.getAbsolutePath}"""".!!

    println(result)
  }

}
