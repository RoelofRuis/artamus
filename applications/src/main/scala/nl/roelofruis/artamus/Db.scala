package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.application.Parser._
import nl.roelofruis.artamus.application.{Application, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.analysis.rna.Model.RNATransition

import scala.io.StdIn

object Db extends App {

  import nl.roelofruis.artamus.application.Printer._

  val program = for {
    tuning      <- SettingsLoader.loadTuning
    rnaRules    <- RNALoader.loadRules(tuning)
    degree      <- tuning.parser(StdIn.readLine("From degree\n >")).parseDegree
    result      = rnaRules.transitions.filter { _.from == degree }
    _           = printTransitions(result, tuning)
  } yield ()

  Application.run(program)

  def printTransitions(transitions: Seq[RNATransition], tuning: Settings): Unit = {
    transitions.map { transition =>
      val textDegreeFrom = tuning.printDegree(transition.from)
      val textDegreeTo = tuning.printDegree(transition.to)
      val weight = transition.weight
      s"$textDegreeFrom -> $textDegreeTo [$weight]"
    }
      .foreach(println)
  }

}