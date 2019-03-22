package interaction.midi

import scala.collection.mutable.ListBuffer

class ResourceContainer(containerName: String) {

  private val closeHooks = ListBuffer[(String, () => Unit)]()

  def close(): Unit = closeHooks.foreach { case (_, hook) => hook() }

  def register(name: String, closeHook: () => Unit): Unit = closeHooks.append((name, closeHook))

}
