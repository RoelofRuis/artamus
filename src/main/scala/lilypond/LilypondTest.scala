package lilypond

import music._

object LilypondTest extends App {

  def notesToLily(notes: List[Note]): String = {
    notes.map { note =>
      pitchToLily(note.midiPitch) + durationToLily(note.duration)
    }.mkString(" ")
  }

  def pitchToLily(midiPitch: MidiPitch): String = {
    Scale.MAJOR_SCALE_MATH.pitchClassToStep(midiPitch.pitchClass) match {
      case Some(Step(0)) => "c"
      case Some(Step(1)) => "d"
      case Some(Step(2)) => "e"
      case Some(Step(3)) => "f"
      case Some(Step(4)) => "g"
      case Some(Step(5)) => "a"
      case Some(Step(6)) => "b"
      case _ => "r"
    }
  }

  def durationToLily(duration: Duration): String = s"${duration.value.n}"

  val notes: List[Note] = List(
    Note(Duration.QUARTER, MidiPitch(64)),
    Note(Duration.QUARTER, MidiPitch(66)),
    Note(Duration.QUARTER, MidiPitch(68)),
    Note(Duration.QUARTER, MidiPitch(69))
  )

  println(notesToLily(notes))

}
