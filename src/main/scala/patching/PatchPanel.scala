package patching

import java.util.concurrent.ConcurrentHashMap

import javax.inject.Inject
import patching.PatchPanel.PatchCable

import scala.util.{Failure, Success}

class PatchPanel @Inject() () {

  private val cables = new ConcurrentHashMap[PatchCableId, PatchCable]()

  def hasPatchCable(id: PatchCableId): Boolean = cables.contains(id)

  def connect[A <: AutoCloseable, B <: AutoCloseable](
    from: A,
    to: B,
    id: Option[PatchCableId] = None
  )(implicit connector: CanConnect[A, B]): Either[PatchingException, PatchCableId] = {
    connector.connect(from, to) match {
      case Failure(ex) => Left(PatchingException(ex))
      case Success(()) =>
        val cableId = id.getOrElse(PatchCableId())
        val cable = PatchCable(from, to)
        cables.put(cableId, cable)
        Right(cableId)
    }
  }

  def disconnect(cableId: PatchCableId): Unit = {
    Option(cables.remove(cableId)) match {
      case Some(cable) =>
        cable.from.close()
        cable.to.close()
      case None =>
    }
  }

}

object PatchPanel {

  final case class PatchCable(
    from: AutoCloseable,
    to: AutoCloseable
  )

}