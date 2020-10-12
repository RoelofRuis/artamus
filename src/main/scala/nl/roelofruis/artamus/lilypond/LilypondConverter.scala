package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.common.{Duration, Position}
import nl.roelofruis.artamus.core.track.Layer.NoteSeq
import nl.roelofruis.artamus.core.track.Pitched.Note
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths.TuningDefinition
import nl.roelofruis.artamus.core.track.algorithms.{TemporalMaths, TunedMaths}
import nl.roelofruis.artamus.lilypond.Grammar.{CME, EqualToPrevious, LilypondDocument, PowerOfTwoWithDots, Relative, TLE, Duration => LilyDuration, Note => LilyNote, Pitch => LilyPitch, Rest => LilyRest}

case class LilypondConverter(settings: TuningDefinition) extends TunedMaths with TemporalMaths {

  def convert(music: LilypondDocument): Either[Throwable, NoteSeq] = convertInternal(music, Position.ZERO, None).get

  private def convertInternal(
    music: LilypondDocument,
    position: Position,
    relativePitch: Option[Note]
  ): MusicState = {
    music
      .foldLeft(MusicState(relativePitch, position)) { case (state, expression) =>
        if (state.error.isDefined) state
        else {
          expression match {
            case cme: CME => state.append(convertInternal(cme.contents, state.position, relativePitch))
            case relative: Relative => state.append(convertInternal(relative.contents.contents, state.position, Some(pitchToNote(relative.to))))
            case note: LilyNote => state.addNote(note)
            case rest: LilyRest => state.addRest(rest)
            case _ => state
          }
        }
      }
  }

  private def pitchToNote(pitch: LilyPitch): Note = {
    Note(pdFromAccidental(pitch.step, pitch.accidentals), pitch.octave)
  }

  private case class MusicState(
    relativePitch: Option[Note],
    position: Position,
    private val noteSeq: NoteSeq = Seq(),
    private val untiedNotes: Seq[LilyNote] = Seq(),
    private val lastDuration: Option[Duration] = None,
    error: Option[Throwable] = None
  ) {
    def append(that: MusicState): MusicState = copy(
      noteSeq = noteSeq ++ that.noteSeq,
      untiedNotes = that.untiedNotes,
      lastDuration = that.lastDuration,
      position = that.position,
      error = that.error orElse error
    )

    def get: Either[Throwable, NoteSeq] = {
      error match {
        case None => Right(noteSeq)
        case Some(msg) => Left(msg)
      }
    }

    def addNote(note: LilyNote): MusicState = {
      val dur = convertDuration(note.duration) orElse lastDuration

      if (note.tie) copy(untiedNotes = untiedNotes :+ note)
      else if (untiedNotes.isEmpty) insertLilyNote(dur, note)
      else {
        val (totalDur, _) = untiedNotes.foldLeft((dur, note.pitch)) { case ((totalDuration, pitch), note) =>
          if (totalDuration.isEmpty || note.pitch != pitch) (None, pitch)
          else (totalDuration.flatMap(d => convertDuration(note.duration).map(_ + d)), pitch)
        }
        totalDur match {
          case None => setError("cannot tie notes with different pitch")
          case Some(dur) => insertLilyNote(Some(dur), note)
        }
      }
    }

    private def insertLilyNote(duration: Option[Duration], note: LilyNote): MusicState = {
      duration match {
        case None => setError("initial duration missing")
        case Some(dur) =>
          val noteGroup = Seq(convertPitch(note.pitch))
          copy(
            relativePitch = relativePitch.map(_ => noteGroup.head),
            position = position + dur,
            noteSeq = noteSeq :+ Windowed(position, dur, noteGroup),
            untiedNotes = Seq(),
            lastDuration = Some(dur),
          )
      }
    }

    private def setError(msg: String): MusicState = copy(error = Some(new Throwable(msg)))

    def addRest(rest: LilyRest): MusicState = {
      convertDuration(rest.duration) orElse lastDuration match {
        case None => setError("initial duration missing")
        case Some(dur) => copy(
          position = position + dur,
          lastDuration = Some(dur),
        )
      }
    }

    private def convertPitch(targetPitch: LilyPitch): Note = {
      relativePitch match {
        case None => pitchToNote(targetPitch)
        case Some(relativePitch) =>
          /* http://lilypond.org/doc/v2.20/Documentation/notation/writing-pitches#relative-octave-entry */
          val relativeStep = relativePitch.descriptor.step
          val targetStep = targetPitch.step
          var stepsDown = relativeStep - targetStep
          if (stepsDown < 0) stepsDown += numSteps
          val moveDown = stepsDown <= numSteps / 2
          val moveOctaveCorrection = {
            if (moveDown && (relativeStep < targetStep)) -1
            else if (!moveDown && (relativeStep > targetStep)) 1
            else 0
          }
          val targetOctave = targetPitch.octave - Constants.BASE_OCTAVE
          val relativeOctave = relativePitch.octave - Constants.BASE_OCTAVE
          val octave = (relativeOctave + moveOctaveCorrection + targetOctave) + Constants.BASE_OCTAVE
          Note(pdFromAccidental(targetPitch.step, targetPitch.accidentals), octave)
      }
    }

    private def convertDuration(duration: LilyDuration): Option[Duration] = duration match {
      case EqualToPrevious() => None
      case PowerOfTwoWithDots(power, dots) => Some(makeDuration(power, dots))
    }
  }

}
