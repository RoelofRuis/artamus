package application.ports

/**
  * An application driver.
  */
trait Driver {

  /**
    * Called after application bootstrapping to start the actual program execution.
    */
  def run(): Unit

}
