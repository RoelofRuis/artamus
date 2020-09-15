package nl.roelofruis.artamus.application.rendering

import java.io.{File, PrintWriter}

import nl.roelofruis.artamus.application.rendering.Model.LilypondSettings
import nl.roelofruis.artamus.core.layout.DisplayableMusic
import nl.roelofruis.artamus.core.track.Track

case class LilypondRenderer(settings: LilypondSettings) {

  private val formatter = LilypondFormatter(settings)

  def render(track: Track): Unit = {
    // to displayable music
    val displayableMusic = DisplayableMusic.fromTrack(track)

    // to lilypond file
    val contents = formatter.write(displayableMusic)

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
