package application.api

/**
  * An application driver.
  */
trait Driver {

  /**
    * Called after application bootstrapping to start the actual program execution.
    *
    * Provides the required busses to communicate with the core.
    */
  def run(commandBus: CommandBus, eventBus: EventBus): Unit

  /**
    * Called on application shutdown, before the driver thread is joined.
    */
  def close(): Unit = {}

}
