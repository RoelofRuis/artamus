package nl.roelofruis.artamus.application

import java.io.{File, PrintWriter}

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.common.Containers.WindowedSeq
import nl.roelofruis.artamus.core.common.Rational
import nl.roelofruis.artamus.core.track.transform.rna.Model.RNAAnalysedChord

object AnalysisCSVWriter {

  import Printer._

  implicit class SettingsOps(settings: Settings) {
    def writeCSV(analysedTrack: WindowedSeq[RNAAnalysedChord], filename: String): Unit = {
      val writer = new PrintWriter(new File(s"src/main/resources/charts/$filename.csv"))
      analysedTrack.foreach { rnaNode =>
        val duration = rnaNode.window.duration
        val timesFit = duration.v % Rational(1, 4)

        // TODO: replace assumption of always fitting Rational(1, 4)
        if (timesFit < 1) throw new NotImplementedError(s"Duration [$duration] does not fit")

        val chordName = settings.printChord(rnaNode.element.chord)
        val degree = settings.printDegree(rnaNode.element.degree)
        val absoluteKey = settings.printKeyDegree(rnaNode.element.absoluteKey)
        val relativeKey = settings.printKey(rnaNode.element.relativeKey)
        writer.write(s"$chordName;$degree;$absoluteKey;$relativeKey\n")
        Range(0, timesFit - 1).foreach(_ => writer.write(";;;\n"))
      }
      writer.close()
    }
  }

}
