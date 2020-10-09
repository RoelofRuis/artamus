package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.core.common.Temporal.Windowed
import nl.roelofruis.artamus.core.common.{Duration, Position}
import nl.roelofruis.artamus.core.track.Layer.NoteSeq
import nl.roelofruis.artamus.core.track.Pitched.{Note, NoteGroup}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths.TuningDefinition
import nl.roelofruis.artamus.core.track.algorithms.{TemporalMaths, TunedMaths}
import nl.roelofruis.artamus.lilypond.Grammar.{CompoundMusicExpression, EqualToPrevious, PowerOfTwoWithDots, Note => LilyNote, Pitch => LilyPitch, Rest => LilyRest}

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
            case LilyNote(LilyPitch(step, accidentals, octave), PowerOfTwoWithDots(power, dots), tie) =>
              val noteGroup = Seq(Note(pdFromAccidental(step, accidentals), octave + 3))
              state.addElement(Some(makeDuration(power, dots)), noteGroup)

            case LilyNote(LilyPitch(step, accidentals, octave), EqualToPrevious(), tie) =>
              val noteGroup = Seq(Note(pdFromAccidental(step, accidentals), octave + 3))
              state.addElement(None, noteGroup)

            case LilyRest(PowerOfTwoWithDots(power, dots)) =>
              state.skipDuration(Some(makeDuration(power, dots)))

            case LilyRest(EqualToPrevious()) =>
              state.skipDuration(None)
          }
        }
      }
      .get
  }

  private case class MusicState(
    noteSeq: NoteSeq = Seq(),
    lastDuration: Option[Duration] = None,
    position: Position = Position.ZERO,
    error: Option[Throwable] = None
  ) {
    def get: Either[Throwable, NoteSeq] = {
      error match {
        case None => Right(noteSeq)
        case Some(msg) => Left(msg)
      }
    }

    def addElement(duration: Option[Duration], noteGroup: NoteGroup): MusicState = {
      duration orElse lastDuration match {
        case None => copy(error = Some(new Throwable("initial duration missing")))
        case Some(dur) => copy(
            noteSeq = noteSeq :+ Windowed(position, dur, noteGroup),
            lastDuration = Some(dur),
            position = position + dur
          )
      }
    }

    def skipDuration(duration: Option[Duration]): MusicState = {
      duration orElse lastDuration match {
        case None => copy(error = Some(new Throwable("initial duration missing")))
        case Some(dur) => copy(
          lastDuration = Some(dur),
          position = position + dur
        )
      }
    }
  }

}
