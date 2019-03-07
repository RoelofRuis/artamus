package interaction.midi

import application.ports.ManagedResource

import scala.collection.mutable.ListBuffer

class ResourceContainer(containerName: String) extends ManagedResource {

  private val closeHooks = ListBuffer[(String, () => Unit)]()

  override def getName: String = closeHooks.map { case (name, _) => s"- [$name]" }.mkString(s"Container [$containerName] Holding:\n", "\n", "")

  override def close(): Unit = closeHooks.foreach { case (_, hook) => hook() }

  def register(name: String, closeHook: () => Unit): Unit = closeHooks.append((name, closeHook))

}
