package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.{Application, SettingsLoader}
import nl.roelofruis.artamus.core.track.Pitched.QualityGroup

object Inspect extends App {

  import nl.roelofruis.artamus.application.Printer._

  def program: ParseResult[Unit] = for {
    tuning <- SettingsLoader.loadTuning
    _      = printQualityGroupMap(tuning.qualityGroupMap, tuning)
  } yield ()

  Application.runRepeated(program)

  def printQualityGroupMap(map: Map[String, QualityGroup], tuning: Settings): Unit = {
    map.foreach { case (name, group) =>
      println(s"Group $name")
      group.qualities.foreach { q =>
        println(tuning.printQuality(q))
      }
    }
  }

}