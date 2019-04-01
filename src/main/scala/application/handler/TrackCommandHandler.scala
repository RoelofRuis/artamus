package application.handler

import application.api.Commands._
import application.api.Events.PlaybackRequest
import application.interact.{ApplicationEventBus, SynchronousCommandBus}
import application.model.event.MidiTrack.Track_ID
import application.model.symbolic.Track
import application.repository.TrackRepository
import application.service.SymbolTrackFactory
import application.service.quantization.TrackQuantizer
import application.service.quantization.TrackQuantizer.Params
import application.service.recording.RecordingManager
import javax.inject.{Inject, Named}

import scala.util.{Success, Try}

// TODO: see if TrackQuantizer and TicksPerQuarter can be moved to a separate service/controller
class TrackCommandHandler @Inject() (
  bus: SynchronousCommandBus,
  trackRepository: TrackRepository,
  quantizer: TrackQuantizer,
  @Named("TicksPerQuarter") recordingResolution: Int,
  recordingManager: RecordingManager,
  symbolTrackFactory: SymbolTrackFactory,
  eventBus: ApplicationEventBus
) {

  bus.subscribeHandler(Handler[GetAll.type](_ => Success(trackRepository.getAll.map(_.id))))
  bus.subscribeHandler(Handler[StartRecording.type](_ => recordingManager.startRecording))
  bus.subscribeHandler(Handler[StoreRecorded](_ => storeRecorded()))
  bus.subscribeHandler(Handler[Quantize](c => quantize(c.trackId, c.subdivision, c.gridErrorMultiplier)))
  bus.subscribeHandler(Handler[Play](c => play(c.trackId)))
  bus.subscribeHandler(Handler[ToSymbolTrack](c => toSymbolTrack(c.trackId)))

  private def storeRecorded(): Try[(Track_ID, Int)] = {
    recordingManager.stopRecording.map { case (ticks, elements) =>
      trackRepository.add(
        ticks,
        elements
      )
    }.map(t => (t.id, t.elements.size))
  }

  private def play(id: Track_ID): Try[Unit] = {
    trackRepository.get(id).map { track =>
      eventBus.publish(PlaybackRequest(track))
    }
  }

  private def quantize(id: Track_ID, subdivision: Int, gridErrorMultiplier: Int): Try[Track_ID] = {
    val quantizationParams = Params(
      recordingResolution / 16,
      recordingResolution * 2,
      gridErrorMultiplier,
      subdivision
    )

    trackRepository.get(id)
      .map { track =>
        val (newTicksPerQuarter, newElements) = quantizer.quantize(track, quantizationParams)

        trackRepository.add(
          newTicksPerQuarter,
          newElements
        )
      }.map(_.id)
  }

  private def toSymbolTrack(id: Track_ID): Try[Track] = {
    trackRepository.get(id).map(symbolTrackFactory.trackToSymbolTrack)
  }
}
