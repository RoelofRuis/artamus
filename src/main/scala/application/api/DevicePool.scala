package application.api

// TODO: move to MIDI and device logic in driver layer
trait DevicePool {

  def getInfo: Array[String]

}
