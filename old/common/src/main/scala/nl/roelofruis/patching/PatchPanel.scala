package nl.roelofruis.patching

import java.util.concurrent.ConcurrentHashMap

import nl.roelofruis.patching.PatchPanel.PatchCable
import patching.{PatchCableId, PatchingException}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

class PatchPanel() {

  type Description = String

  private val cables = new ConcurrentHashMap[PatchCableId, (Description, PatchCable)]()

  def hasPatchCable(id: PatchCableId): Boolean = cables.contains(id)

  def connect[A <: AutoCloseable, B <: AutoCloseable](
    from: A,
    to: B,
    description: Description,
    id: Option[PatchCableId] = None
  )(implicit connector: CanConnect[A, B]): Either[PatchingException, PatchCableId] = {
    connector.connect(from, to) match {
      case Failure(ex) => Left(PatchingException(ex))
      case Success(()) =>
        val cableId = id.getOrElse(PatchCableId())
        val cable = PatchCable(from, to)
        cables.put(cableId, (description, cable))
        Right(cableId)
    }
  }

  def disconnect(cableId: PatchCableId): Unit = {
    Option(cables.remove(cableId)) match {
      case Some((_, cable)) =>
        cable.from.close()
        cable.to.close()
      case None =>
    }
  }

  def viewConnections: List[(Description, PatchCableId)] = {
    cables.asScala.toList.map { case (id, (descr, _)) => (descr, id)}
  }

}

object PatchPanel {

  final case class PatchCable(
    from: AutoCloseable,
    to: AutoCloseable
  )

}