package client.gui

import java.awt.event.{WindowAdapter, WindowEvent}

import client.infra.{Callback, Client}
import client.{CommandExecutor, ModuleLifetimeHooks}
import artamus.core.api.Control.{Authenticate, Commit}
import artamus.core.api.Display.Render
import artamus.core.api.Event
import artamus.core.api.Write.NewWorkspace
import javax.inject.Inject
import nl.roelofruis.pubsub.Dispatcher

import scala.swing.Swing

class Editor @Inject() (
  moduleLifetimeHooks: ModuleLifetimeHooks, // Expand only as more hooks are needed
  executor: CommandExecutor,
  dispatcher: Dispatcher[Callback, Event],
  client: Client
) extends Thread {

  private def _autoLoginDev(): Unit = {
    import _root_.client.infra.ClientInteraction._

    Thread.sleep(1000)

    client.sendCommandList(List(
      Authenticate("artamus"),
      NewWorkspace,
      Render,
      Commit()
    ))
  }

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

    _autoLoginDev()

    frameLock.synchronized {
      try {
        frameLock.wait()
      } catch {
        case ex: InterruptedException => ex.printStackTrace()
      }
    }
  }
}
