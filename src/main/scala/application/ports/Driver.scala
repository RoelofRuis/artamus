package application.ports

/**
  * An application driver.
  */
trait Driver {

  /**
    * Called after application bootstrapping to start the actual program execution.
    *
    * Drivers should use the message bus to communicate with the core.
    */
  def run(messageBus: MessageBus): Unit

}
