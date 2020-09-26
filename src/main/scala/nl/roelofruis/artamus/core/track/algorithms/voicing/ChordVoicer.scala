package nl.roelofruis.artamus.core.track.algorithms.voicing

import nl.roelofruis.artamus.application.Model.Settings
import nl.roelofruis.artamus.core.common.Containers.{Windowed, WindowedSeq}
import nl.roelofruis.artamus.core.common.algorithms.GraphSearch
import nl.roelofruis.artamus.core.track.Layer.{ChordTrack, NoteTrack}
import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.algorithms.NoteMaths

case class ChordVoicer(settings: Settings) extends NoteMaths {

  type WindowedNoteGroup = Windowed[NoteGroup]

  def voiceChords(chordTrack: ChordTrack): NoteTrack = {
    val analysis = GraphSearch.bestFirst(
      1,
      findPossibleNodes,
      scoreTransition
    )(chordTrack)

    analysis match {
      case Some(result) if result.length >= 1 => result.toSeq
      case _ => WindowedSeq.empty
    }
  }

  def findPossibleNodes: Windowed[Chord] => List[WindowedNoteGroup] = chordWindow => {
    val intervals = chordWindow.get.quality.intervals.map { _ + chordWindow.get.root }
    val octaves = Range.inclusive(2, 5)

    combineSequences(intervals, octaves)
      .map { chord =>
        chord.map { case (pd, oct) => Note(pd, oct) }
      }
      .filter { notes =>
        val bassInRange = notes.orderedMidiNumbers.headOption.exists(i => i < 50)
        val nonBassDifferences = notes.orderedMidiDifferences.tail
        val intervalTooSmall = nonBassDifferences.minOption.exists(_ < 2)
        val intervalTooLarge = nonBassDifferences.maxOption.exists(_ > 10)
        bassInRange && ! intervalTooSmall && ! intervalTooLarge
      }
      .map { Windowed(chordWindow.window, _) }
      .toList
  }

  def scoreTransition: (WindowedNoteGroup, WindowedNoteGroup) => Option[Int] = (currentVoicing, nextVoicing) => {
    val group1 = currentVoicing.get.orderedMidiNumbers
    val group2 = nextVoicing.get.orderedMidiNumbers

    val score = group1.map { nr =>
      if (group2.contains(nr)) 2
      else if (group2.contains(nr + 1) || group2.contains(nr - 1)) 1
      else 0
    }.sum

    Some(score)
  }

  private def combineSequences[A, B](s1: Seq[A], s2: Seq[B]): Iterator[Seq[(A, B)]] = {
    val n = s1.length
    Seq.fill(n)(s2)
      .flatten
      .combinations(n)
      .flatMap(_.permutations)
      .map(s1.zip(_))
  }

}
