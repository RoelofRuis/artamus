package client.io.terminal

import client.io.IOLifetimeManager

class TerminalIOLifetimeManager() extends IOLifetimeManager {
  override def initializeAll(): Unit = {}
  override def closeAll(): Unit = {}
}
