package client.midi

import javax.sound.midi.{MidiMessage, Receiver}

import scala.collection.mutable.ListBuffer

class BufferedMidiMessageReceiver extends Receiver {

  private val messageBuffer: ListBuffer[MidiMessage] = ListBuffer[MidiMessage]()
  private var isListening = false

  def startReading(): Unit = {
    messageBuffer.clear()
    isListening = true
  }

  def stopReading(): List[MidiMessage] = {
    isListening = false
    messageBuffer.toList
  }

  override def send(message: MidiMessage, timeStamp: Long): Unit = {
    if (isListening) {
      messageBuffer.append(message)
    }
  }

  override def close(): Unit = ()
}
