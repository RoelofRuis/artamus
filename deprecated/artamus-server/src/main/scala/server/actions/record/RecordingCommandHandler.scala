package server.actions.record

import artamus.core.api.Record.{ClearRecording, Quantize, RecordNote, SetFormalisationProfile}
import artamus.core.math.temporal.{Duration, Position, Window}
import artamus.core.model.primitives.{Note, NoteGroup}
import artamus.core.model.recording.Recording
import artamus.core.ops.formalise.FormalisationProfile
import artamus.core.model.track.layers.{LayerData, NoteLayer, RhythmLayer}
import javax.inject.{Inject, Singleton}
import server.api.{CommandHandlerRegistration, CommandRequest}

@Singleton
private[server] class RecordingCommandHandler @Inject() (
  registry: CommandHandlerRegistration,
  storage: RecordingStorage,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  registry.register[ClearRecording] { req =>
    storage.startRecording(req.user.id)

    CommandRequest.ok
  }

  registry.register[SetFormalisationProfile] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      newWorkspace = workspace.copy(profile=req.attributes.profile)
      _ <- req.db.saveWorkspace(newWorkspace)
    } yield ()

    CommandRequest.dbResult(res)
  }

  registry.register[RecordNote] { req =>
    storage.recordNote(req.user.id, req.attributes.note)

    CommandRequest.ok
  }

  import artamus.core.ops.formalise.Quantization._
  import artamus.core.ops.edit.analysis.TwelveToneTuning._
  registry.register[Quantize] { req =>
    storage.getRecording(req.user.id) match {
      case None => CommandRequest.ok
      case Some(recording) =>
        if (recording.notes.isEmpty) CommandRequest.ok
        else {
          val res = for {
            workspace <- req.db.getWorkspaceByOwner(req.user)
            currentTrack <- req.db.getTrackById(workspace.editingTrack)
            newLayer = transferRecording(workspace.profile, recording)
            newTrack = currentTrack.appendLayerData(newLayer)
            _ <- req.db.saveTrack(newTrack)
          } yield ()
          CommandRequest.dbResult(res)
        }
    }
  }

  // TODO: extract track writing logic
  private def transferRecording(
    recordTransfer: FormalisationProfile,
    recording: Recording,
  ): LayerData = {
    val quantized = recordTransfer.quantizer.quantize(recording)
    val notePositions: Map[Position, (Seq[Note], Duration)] = recording
      .notes
      .zip(quantized.zip(quantized.drop(1).appended(quantized.last + recordTransfer.lastNoteDuration)))
      .foldLeft(Map[Position, (Seq[Note], Duration)]()) { case (acc, (rawMidiNote, (position, nextPosition))) =>
        val duration = nextPosition - position // TODO: determine proper duration
        val note = Note(rawMidiNote.noteNumber.toOct, rawMidiNote.noteNumber.toPc)
        acc.updatedWith(position) {
          case None => Some(Seq(note), Seq(Duration.ZERO, duration).max)
          case Some((seq, prevDur)) => Some(seq :+ note, Seq(duration, prevDur).max)
        }
      }

    if (recordTransfer.rhythmOnly) notePositions.foldLeft(RhythmLayer()) {
      case (l, (pos, (notes, duration))) => l.writeNoteGroup(NoteGroup(Window(pos, duration), notes))
    }
    else notePositions.foldLeft(NoteLayer()) {
      case (l, (pos, (notes, duration))) => l.writeNoteGroupToDefaultVoice(NoteGroup(Window(pos, duration), notes))
    }
  }

}
