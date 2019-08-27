package interaction.midi.device

import server.model.Track

import scala.util.Try

/**
  * Input device for reading symbolic music.
  */
trait RecordingDevice {

  def start(ticksPerQuarter: Int): Try[Unit]

  def stop(): Try[Track]

}
