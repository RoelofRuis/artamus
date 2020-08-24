package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model._

import scala.annotation.tailrec
import scala.collection.mutable

case class RNA(tuning: Tuning, rules: RNARules) extends TuningMaths {

  import nl.roelofruis.artamus.tuning.Printer._ // TODO: remove

  private sealed trait StateType { val weight: Int }
  private final case class EndState(weight: Int) extends StateType
  private final case class State(
    chord: Chord,
    keyInterval: PitchDescriptor,
    key: Key,
    weight: Int
  ) extends StateType {
    def degreePitch: PitchDescriptor = chord.root - key.root
  }

  private final case class Graph(
    nodeList: List[Chord],
    stateList: Seq[StateType]
  ) extends Ordered[Graph] {
    val score: Int = stateList.foldRight(0) { case (state, sum) => sum + state.weight}
    override def compare(that: Graph): Int = score.compareTo(that.score)
  }

  def nameDegrees(chords: Seq[Chord], root: PitchDescriptor): Seq[Degree] = {

    val (successes, failures) = graphSearch(chords, root, 1, true)

    printGraphs(successes.dequeueAll)
    printGraphs(failures.dequeueAll)

    def printGraphs(graphs: Seq[Graph]): Unit = {
      graphs.foreach { graph =>
        println("> ")
        graph.stateList.map {
          case EndState(weight) => s"END [$weight]"
          case s @ State(chord, keyInterval, key, weight) =>
            val textChord = tuning.printChord(chord)
            val textDegree = tuning.printDegreeDescriptor(s.degreePitch)
            val textKey = tuning.printKey(key)
            val textKeyInterval = tuning.printIntervalDescriptor(keyInterval)
            s"$textChord: $textDegree in $textKey ($textKeyInterval) [$weight]"
        }.foreach(println)
      }
    }

    Seq()
  }

  private def graphSearch(
    chords: Seq[Chord],
    root: PitchDescriptor,
    resultsRequired: Int,
    includeFailures: Boolean
  ): (mutable.PriorityQueue[Graph], mutable.PriorityQueue[Graph]) = {
    val inputStates = new mutable.PriorityQueue[Graph]()
    inputStates.enqueue(Graph(chords.toList, Seq()))
    val failures = new mutable.PriorityQueue[Graph]()
    val successes = new mutable.PriorityQueue[Graph]()

    @tailrec
    def search: (mutable.PriorityQueue[Graph], mutable.PriorityQueue[Graph]) = {
      if (successes.size >= resultsRequired) {
        println(s"Search terminated with [${successes.size}] results")
        (failures, successes)
      } else if (inputStates.isEmpty) {
        println("Search exhausted")
        (failures, successes)
      } else {
        val graph = inputStates.dequeue()
        graph.nodeList match {
          case Nil =>
            val completedGraphs = findApplicableTransitions(graph.stateList.lastOption, ending = true)
              .flatMap {
                case TransitionEnd(_, weight) => Seq(EndState(weight))
                case _ => Seq()
              }
              .map(state => Graph(Nil, graph.stateList :+ state))

            if (completedGraphs.isEmpty && includeFailures) failures.enqueue(graph)
            else successes.enqueue(completedGraphs: _*)
            search

          case chord :: tail =>
            val newGraphs = findApplicableTransitions(graph.stateList.lastOption, ending = false)
              .flatMap {
                case TransitionStart(nextState, weight) => findNextStates(chord, nextState, root, weight)
                case Transition(_, nextState, weight) => findNextStates(chord, nextState, root, weight)
                case _ => Seq()
              }
              .map(state => Graph(tail, graph.stateList :+ state))

            if (newGraphs.isEmpty && includeFailures) failures.enqueue(graph)
            else newGraphs.foreach(g => inputStates.enqueue(g))
            search
        }
      }
    }

    search
  }

  private def findNextStates(
    chord: Chord,
    transition: TransitionDescription,
    root: PitchDescriptor,
    weight: Int
  ): Seq[State] = {
    val allowedKeys = for {
      keyRoot <- getAllPitchDescriptors.filter(allowedIntervals(transition.keyInterval)).map(_ + root)
      scale <- tuning.scaleMap.values.filter(allowedScales(transition.scale))
    } yield Key(keyRoot, scale)

    allowedKeys.flatMap { key =>
      if (key.contains(chord)) Some(
        State(
          chord,
          key.root - root,
          key,
          weight
        )
      )
      else None
    }
      .filter(state => allowedDegrees(transition.degree)(state.degreePitch))
  }

  private def allowedIntervals(filter: AllowedKeyInterval): PitchDescriptor => Boolean = descriptor => {
    filter match {
      case AnyKeyInterval => true
      case SpecificKeyInterval(filterDescriptor) => filterDescriptor == descriptor
    }
  }

  private def allowedScales(filter: AllowedScale): Scale => Boolean = scale => {
    filter match {
      case AnyScale => true
      case SpecificScale(filterScale) => filterScale == scale
    }
  }

  private def allowedDegrees(filter: AllowedDegree): PitchDescriptor => Boolean = descriptor => {
    filter match {
      case AnyDegree => true
      case SpecificDegree(filterDegree) => filterDegree == descriptor
    }
  }

  private def findApplicableTransitions(currentState: Option[StateType], ending: Boolean): List[TransitionType] = {
    currentState match {
      case None => rules.transitions.collect { case s: TransitionStart => s }
      case Some(state: State) =>
        if ( ! ending) {
          rules
            .transitions
            .collect { case s: Transition => s }
            .filter(s => allowedCurrentState(s.currentState, state))
        } else {
          rules
            .transitions
            .collect { case s: TransitionEnd => s }
            .filter(s => allowedCurrentState(s.currentState, state))
        }
    }
  }

  private def allowedCurrentState(transition: TransitionDescription, state: State): Boolean = {
    val validDegree = transition.degree match {
      case AnyDegree => true
      case SpecificDegree(degree) => state.degreePitch == degree
    }
    val validKeyInterval = transition.keyInterval match {
      case AnyKeyInterval => true
      case SpecificKeyInterval(interval) => state.keyInterval == interval
    }
    val validScale = transition.scale match {
      case AnyScale => true
      case SpecificScale(scale) => state.key.scale == scale
    }
    validDegree && validKeyInterval && validScale
  }

}
