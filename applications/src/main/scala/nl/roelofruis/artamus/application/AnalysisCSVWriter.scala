package nl.roelofruis.artamus.application

import java.io.{File, PrintWriter}

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.analysis.rna.Model.RNAAnalysedChord

object AnalysisCSVWriter {

  import Printer._

  implicit class SettingsOps(settings: Settings) {
    def writeCSV(analysis: Option[Array[RNAAnalysedChord]], filename: String): Unit = {
      analysis match {
        case None =>
        case Some(analysis) =>
          val writer = new PrintWriter(new File(s"applications/charts/$filename.csv"))
          analysis.foreach { rnaNode =>
            val chordName = settings.printChord(rnaNode.chord)
            val degree = settings.printDegree(rnaNode.degree)
            val absoluteKey = settings.printKeyDegree(rnaNode.absoluteKey)
            val relativeKey = settings.printKey(rnaNode.relativeKey)
            writer.write(s"$chordName;$degree;$absoluteKey;$relativeKey\n")
          }
          writer.close()
      }
    }
  }

}
