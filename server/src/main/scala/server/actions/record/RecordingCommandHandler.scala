package server.actions.record

import domain.interact.Record.{ClearRecording, Quantize, RecordNote}
import domain.math.temporal.{Duration, Position, Window}
import domain.primitives.{Note, NoteGroup}
import domain.record.{Quantization, Quantizer}
import domain.write.layers.{NoteLayer, RhythmLayer}
import javax.inject.{Inject, Singleton}
import server.actions.Responses
import server.infra.ServerDispatcher

@Singleton
private[server] class RecordingCommandHandler @Inject() (
  dispatcher: ServerDispatcher,
  storage: RecordingStorage,
) {

  dispatcher.subscribe[ClearRecording] { req =>
    storage.startRecording(req.user.id)

    Responses.ok
  }

  dispatcher.subscribe[RecordNote] { req =>
    storage.recordNote(req.user.id, req.attributes.note)

    Responses.ok
  }

  // TODO: extract track writing logic
  import Quantization._
  import domain.write.analysis.TwelveToneTuning._
  import server.model.Tracks._
  import server.model.Workspaces._
  dispatcher.subscribe[Quantize] { req =>
    storage.getRecording(req.user.id) match {
      case None => Responses.ok
      case Some(recording) =>
        if (recording.notes.nonEmpty) {
          val quantized = req.attributes.customQuantizer.getOrElse(Quantizer()).quantize(recording.notes.map(_.starts))
          val notePositions: Map[Position, (Seq[Note], Duration)] = recording
            .notes
            .zip(quantized.zip(quantized.drop(1).appended(quantized.last + req.attributes.lastNoteDuration)))
            .foldLeft(Map[Position, (Seq[Note], Duration)]()) { case (acc, (rawMidiNote, (position, nextPosition))) =>
              val duration = nextPosition - position // TODO: determine proper duration
              val note = Note(rawMidiNote.noteNumber.toOct, rawMidiNote.noteNumber.toPc)
              acc.updatedWith(position) {
                case None => Some(Seq(note), Seq(Duration.ZERO, duration).max)
                case Some((seq, prevDur)) => Some(seq :+ note, Seq(duration, prevDur).max)
              }
            }

          val newLayer = if (req.attributes.rhythmOnly) notePositions.foldLeft(RhythmLayer()) {
            case (l, (pos, (notes, duration))) => l.writeNoteGroup(NoteGroup(Window(pos, duration), notes))
          }
          else notePositions.foldLeft(NoteLayer()) {
            case (l, (pos, (notes, duration))) => l.writeNoteGroup(NoteGroup(Window(pos, duration), notes))
          }

          val res = for {
            workspace <- req.db.getWorkspaceByOwner(req.user)
            currentTrack <- req.db.getTrackById(workspace.editingTrack)
            newTrack = currentTrack.appendLayerData(newLayer)
            _ <- req.db.saveTrack(newTrack)
          } yield ()

          Responses.executed(res)
        }
        else Responses.ok
    }
  }

}