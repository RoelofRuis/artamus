package nl.roelofruis.artamus

import nl.roelofruis.artamus.application.Model.{ParseResult, Settings}
import nl.roelofruis.artamus.application.{Application, RNALoader, SettingsLoader}
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.RNATransition

object Db extends App {

  import nl.roelofruis.artamus.application.Printer._
  import nl.roelofruis.artamus.application.Reader._

  def program: ParseResult[Unit] = for {
    tuning      <- SettingsLoader.loadTuning
    rnaRules    <- RNALoader.loadRules(tuning)
    degree      <- tuning.readDegree
    result      = rnaRules.transitions.filter { _.from == degree }
    _           = printTransitions(result, tuning)
  } yield ()

  Application.runRepeated(program)

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