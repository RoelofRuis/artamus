package nl.roelofruis.artamus.core.track.algorithms.functional

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.track.Layer.ChordSeq
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Quality}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.core.track.algorithms.functional.Model.{AnyIntervalOnStep, ExactInterval, FunctionalAnalysisRules, QualityTag}

case class FunctionalAnalyser(settings: Settings, rules: FunctionalAnalysisRules) extends TunedMaths {

  import nl.roelofruis.artamus.application.Printer._

  def analyse(chordTrack: ChordSeq): Unit = {
    chordTrack.mapVal { chord =>
      val tags = matchingTags(chord.quality)
      println(settings.printChord(chord))
      println(tags)

    }
  }

  private def matchingTags(quality: Quality): Seq[QualityTag] = {
    rules.tagReductions.map { reduction =>
      val score = reduction.intervals.foldLeft(0) { case (acc, descr) =>
        if (acc == -1) acc
        else {
          val contains = {
            descr.interval match {
              case AnyIntervalOnStep(step) => quality.intervals.map(_.step).contains(step)
              case ExactInterval(interval) => quality.intervals.contains(interval)
            }
          }
          if (! descr.shouldContain && contains) -1
          else {
            if (contains) acc + 1 else acc
          }
        }
      }
      (score, reduction.possibleTags)
    }
      .filter { case (score, _) => score >= 0 }
      .maxByOption { case (score, _) => score }
      .map(_._2).getOrElse(Seq())
  }

}
