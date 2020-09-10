package nl.roelofruis.artamus.application

import java.io.{File, PrintWriter}

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.analysis.rna.Model.RNAAnalysedChord
import nl.roelofruis.artamus.core.primitives.Rational

object AnalysisCSVWriter {

  import Printer._

  implicit class SettingsOps(settings: Settings) {
    def writeCSV(analysis: Option[Array[RNAAnalysedChord]], filename: String): Unit = {
      analysis match {
        case None =>
        case Some(analysis) =>
          val writer = new PrintWriter(new File(s"applications/charts/$filename.csv"))
          analysis.foreach { rnaNode =>
            val duration = rnaNode.chord.window.duration
            val timesFit = duration.v % Rational(1, 4)

            // TODO: replace assumption of always fitting Rational(1, 4)
            if (timesFit < 1) throw new NotImplementedError(s"Duration [$duration] does not fit")

            val chordName = settings.printChord(rnaNode.chord.element)
            val degree = settings.printDegree(rnaNode.degree)
            val absoluteKey = settings.printKeyDegree(rnaNode.absoluteKey)
            val relativeKey = settings.printKey(rnaNode.relativeKey)
            writer.write(s"$chordName;$degree;$absoluteKey;$relativeKey\n")
            Range(0, timesFit - 1).foreach(_ => writer.write(";;;\n"))
          }
          writer.close()
      }
    }
  }

}
