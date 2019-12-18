package music.model.record

sealed trait RecordingMode

/** Only store newly received note events */
case object InputOnly extends RecordingMode

// TODO: add these cases later
///** Practice against the track with the given id. */
//case class Practice(trackId: TrackId) extends RecordingMode
///** Infer a new track from this recording */
//case object InferWritten extends RecordingMode
