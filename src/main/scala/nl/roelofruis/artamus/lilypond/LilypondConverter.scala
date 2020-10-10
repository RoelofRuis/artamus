package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.common.{Duration, Position}
import nl.roelofruis.artamus.core.track.Layer.NoteSeq
import nl.roelofruis.artamus.core.track.Pitched.{Note, NoteGroup}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths.TuningDefinition
import nl.roelofruis.artamus.core.track.algorithms.{TemporalMaths, TunedMaths}
import nl.roelofruis.artamus.lilypond.Grammar.{CompoundMusicExpression, EqualToPrevious, PowerOfTwoWithDots, Duration => LilyDuration, Note => LilyNote, Rest => LilyRest}

case class LilypondConverter(settings: TuningDefinition) extends TunedMaths with TemporalMaths {

  def convert(music: CompoundMusicExpression): Either[Throwable, NoteSeq] = {
    music
      .filter { // Things with duration
        case _: LilyNote => true
        case _: LilyRest => true
        case _ => false
      }
      .foldLeft(MusicState()) { case (state, expression) =>
        if (state.error.isDefined) state
        else {
          expression match {
            case note: LilyNote => state.addNote(note)
            case rest: LilyRest => state.addRest(rest)
          }
        }
      }
      .get
  }

  private case class MusicState(
    private val noteSeq: NoteSeq = Seq(),
    private val untiedNotes: Seq[LilyNote] = Seq(),
    private val lastDuration: Option[Duration] = None,
    private val position: Position = Position.ZERO,
    error: Option[Throwable] = None
  ) {
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
          val noteGroup = Seq(Note(pdFromAccidental(note.pitch.step, note.pitch.accidentals), note.pitch.octave + 3))
          copy(
            noteSeq = noteSeq :+ Windowed(position, dur, noteGroup),
            untiedNotes = Seq(),
            lastDuration = Some(dur),
            position = position + dur
          )
      }
    }

    private def setError(msg: String): MusicState = copy(error = Some(new Throwable(msg)))

    def addRest(rest: LilyRest): MusicState = {
      convertDuration(rest.duration) orElse lastDuration match {
        case None => setError("initial duration missing")
        case Some(dur) => copy(
          lastDuration = Some(dur),
          position = position + dur
        )
      }
    }

    private def convertDuration(duration: LilyDuration): Option[Duration] = duration match {
      case EqualToPrevious() => None
      case PowerOfTwoWithDots(power, dots) => Some(makeDuration(power, dots))
    }
  }

}
