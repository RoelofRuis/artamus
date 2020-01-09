package client.gui

import java.awt.event.{WindowAdapter, WindowEvent}

import client.CommandExecutor
import client.io.IOLifetimeManager
import javax.inject.Inject
import protocol.Event
import pubsub.{Callback, Dispatcher}

import scala.swing.Swing

class Editor @Inject() (
  lifetimeManager: IOLifetimeManager,
  executor: CommandExecutor,
  dispatcher: Dispatcher[Callback, Event]
) extends Thread {

  override def run(): Unit = {
    lifetimeManager.initializeAll()
    val frameLock = new Object
    Swing.onEDT {
      val frame = new EditorLogic(executor, dispatcher)
      frame.peer.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE)
      frame.peer.addWindowListener(new WindowAdapter {
        override def windowClosing(e: WindowEvent): Unit = {
          frameLock.synchronized {
            frameLock.notifyAll()
            frame.dispose()
            lifetimeManager.closeAll()
          }
        }
      })
    }
    frameLock.synchronized {
      try {
        frameLock.wait()
      } catch {
        case ex: InterruptedException => ex.printStackTrace()
      }
    }
  }
}
