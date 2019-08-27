package server.handler

import javax.inject.Inject

// TODO: Refactor entirely
class TrackCommandHandler @Inject() (
//  bus: SocketCommandBus,
//  trackRepository: TrackRepository,
//  quantizer: TrackQuantizer,
//  @Named("TicksPerQuarter") recordingResolution: Int,
//  recordingManager: RecordingManager,
//  eventBus: ApplicationEventBus
) {

//  bus.subscribeHandler(Handler[GetAll.type](_ => Success(trackRepository.getAllIds)))
//  bus.subscribeHandler(Handler[StartRecording.type](_ => recordingManager.startRecording))
//  bus.subscribeHandler(Handler[StoreRecorded](_ => storeRecorded()))
//  bus.subscribeHandler(Handler[Quantize](c => quantize(c.trackId, c.subdivision, c.gridErrorMultiplier)))
//  bus.subscribeHandler(Handler[Play](c => play(c.trackId)))
//  bus.subscribeHandler(Handler[GetTrack](c => toSymbolTrack(c.trackId)))

//  private def storeRecorded(): Try[(TrackID, Int)] = {
//    recordingManager
//      .stopRecording
//      .map(trackRepository.add)
//      .map { case (newId, track) => (newId, track.numSymbols) }
//  }
//
//  private def play(id: TrackID): Try[Unit] = {
//    trackRepository.get(id).map { track =>
//      eventBus.publish(PlaybackRequest(track))
//    }
//  }
//
//  private def quantize(id: TrackID, subdivision: Int, gridErrorMultiplier: Int): Try[TrackID] = {
//    val quantizationParams = Params(
//      recordingResolution / 16,
//      recordingResolution * 2,
//      gridErrorMultiplier,
//      subdivision
//    )
//
//    trackRepository.get(id)
//      .map { track =>
//        val quantizedTrack = quantizer.quantize(track, quantizationParams)
//
//        trackRepository.add(quantizedTrack)
//      }.map { case (newId, _) => newId }
//  }
//
//  private def toSymbolTrack(id: TrackID): Try[Track] = trackRepository.get(id)
}
