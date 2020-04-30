package client.gui

import java.awt.event.{WindowAdapter, WindowEvent}

import client.infra.{Callback, Client}
import client.{CommandExecutor, ModuleLifetimeHooks}
import domain.interact.Control.{Authenticate, Commit}
import domain.interact.Display.Render
import domain.interact.Event
import domain.interact.Write.NewWorkspace
import javax.inject.Inject
import pubsub.Dispatcher

import scala.swing.Swing

class Editor @Inject() (
  moduleLifetimeHooks: ModuleLifetimeHooks, // Expand only as more hooks are needed
  executor: CommandExecutor,
  dispatcher: Dispatcher[Callback, Event]
) extends Thread {

  override def run(): Unit = {
    moduleLifetimeHooks.initializeAll()
    val frameLock = new Object
    Swing.onEDT {
      val frame = new EditorLogic(executor, dispatcher)
      frame.peer.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE)
      frame.peer.addWindowListener(new WindowAdapter {
        override def windowClosing(e: WindowEvent): Unit = {
          frameLock.synchronized {
            frameLock.notifyAll()
            frame.dispose()
            moduleLifetimeHooks.closeAll()
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
