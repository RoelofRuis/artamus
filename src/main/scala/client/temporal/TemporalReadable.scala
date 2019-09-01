package client.temporal

import java.util.concurrent.BlockingQueue

trait TemporalReadable[A] {

  def startReading(): BlockingQueue[A]

  def stopReading(): Unit

}