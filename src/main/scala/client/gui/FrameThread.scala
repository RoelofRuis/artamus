package client.gui

import java.awt.event.{WindowAdapter, WindowEvent}

import scala.swing.Frame

class FrameThread(frame: Frame, disposeCallback: () => Unit) extends Thread {

  private val frameLock = new Object

  override def run(): Unit = {
    frameLock.synchronized {
      try {
        frameLock.wait()
      } catch {
        case ex: InterruptedException => ex.printStackTrace()
      }
    }
  }

  frame.peer.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE)
  frame.peer.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      frameLock.synchronized {
        frameLock.notifyAll()
        disposeCallback()
      }
    }
  })

}
