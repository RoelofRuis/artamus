package nl.roelofruis.artamus.analysis.rna

import nl.roelofruis.artamus.analysis.TuningMaths
import nl.roelofruis.artamus.analysis.rna.Model._
import nl.roelofruis.artamus.degree.Model._

import scala.annotation.tailrec
import scala.collection.mutable

case class RNA(tuning: Tuning, rules: RNARules) extends TuningMaths {

  private sealed trait StateType { val weight: Int }
  private final case class EndState(weight: Int) extends StateType
  private final case class State(
    chord: Chord,
    degreePitch: PitchDescriptor,
    keyInterval: PitchDescriptor,
    key: Key,
    weight: Int
  ) extends StateType

  private final case class Graph(
    nodeList: List[Chord],
    stateList: Seq[StateType]
  ) extends Ordered[Graph] {
    val score: Int = stateList.foldRight(0) { case (state, sum) => sum + state.weight}
    override def compare(that: Graph): Int = score.compareTo(that.score)
  }

  def nameDegrees(chords: Seq[Chord], root: PitchDescriptor): Seq[Degree] = {
    val queue = new mutable.PriorityQueue[Graph]()
    queue.enqueue(Graph(chords.toList, Seq()))

    val resultsRequired = 1

    @tailrec
    def transition(queue: mutable.PriorityQueue[Graph], results: Seq[Graph]): Seq[Graph] = {
      if (results.size >= resultsRequired) {
        println(s"Search terminated with [${results.size}] results")
        results
      } else if (queue.isEmpty) {
        println("Search exhausted")
        results
      } else {
        val graph = queue.dequeue()
        graph.nodeList match {
          case Nil =>
            val completedGraphs = findApplicableTransitions(graph.stateList.headOption, ending = true)
              .flatMap {
                case TransitionEnd(_, weight) => Seq(EndState(weight))
                case _ => Seq()
              }
              .map(state => Graph(Nil, graph.stateList :+ state))

            println(s"Found ${completedGraphs.size} completed graphs")
            transition(queue, results ++ completedGraphs)

          case chord :: tail =>
            val newGraphs = findApplicableTransitions(graph.stateList.headOption, ending = false)
              .flatMap {
                case TransitionStart(nextState, weight) => findNextStates(chord, nextState, root, weight)
                case Transition(_, nextState, weight) => findNextStates(chord, nextState, root, weight)
                case _ => Seq()
              }
              .map(state => Graph(tail, graph.stateList :+ state))

            println(s"Found ${newGraphs.size} expansions")
            newGraphs.foreach(graph => queue.enqueue(graph))
            transition(queue, results)
        }
      }
    }

    val res = transition(queue, Seq())

    res.foreach { graph =>
      import nl.roelofruis.artamus.tuning.Printer._ // TODO: remove
      graph.stateList.map {
        case EndState(_) => "END"
        case State(chord, degreePitch, keyInterval, key, _) =>
          val textChord = tuning.printChord(chord)
          val textDegree = tuning.printDegreeDescriptor(degreePitch)
          val textKey = tuning.printKey(key)
          val textKeyInterval = tuning.printIntervalDescriptor(keyInterval)
          s"$textChord: $textDegree in $textKey ($textKeyInterval)"
      }.foreach(println)
    }

    Seq()
  }

  private def findNextStates(
    chord: Chord,
    transition: TransitionDescription,
    root: PitchDescriptor,
    weight: Int
  ): Seq[State] = {
    val allowedKeys = for {
      keyRoot <- getAllPitchDescriptors.map(_ + root).filter(allowedIntervals(transition.keyInterval))
      scale <- tuning.scaleMap.values.filter(allowedScales(transition.scale))
    } yield Key(keyRoot, scale)

    allowedKeys.flatMap { key =>
      if (key.contains(chord)) Some(
        State(
          chord,
          chord.root - key.root,
          key.root - root,
          key,
          weight
        )
      )
      else None
    }
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

  private def findApplicableTransitions(currentState: Option[StateType], ending: Boolean): List[TransitionType] = {
    currentState match {
      case None =>
        rules.transitions.collect { case s: TransitionStart => s }
      case Some(state: State) =>
        if (ending) {
          rules
            .transitions
            .collect { case s: TransitionEnd => s }
            .filter(s => allowedCurrentState(s.currentState, state))
        }
        else {
          rules
            .transitions
            .collect { case s: Transition => s }
            .filter(s => allowedCurrentState(s.currentState, state))
        }
    }
  }

  private def allowedCurrentState(description: TransitionDescription, state: State): Boolean = {
    description.degree match {
      case AnyDegree => true
      case SpecificDegree(degree) => state.degreePitch == degree
    }
  }

}
