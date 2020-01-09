package patching

import java.util.concurrent.ConcurrentHashMap

import javax.inject.Inject
import patching.PatchPanel.PatchCable
import scala.jdk.CollectionConverters._

import scala.util.{Failure, Success}

class PatchPanel @Inject() () {

  private val cables = new ConcurrentHashMap[PatchCableId, (String, PatchCable)]()

  def hasNamedPatch(name: String): Boolean = {
    cables.asScala.exists { case (_, (name, _)) => name == name }
  }

  def connect[A <: AutoCloseable, B <: AutoCloseable](
    from: A,
    to: B,
    name: Option[String] = None
  )(implicit connector: CanConnect[A, B]): Either[PatchingException, PatchCableId] = {
    connector.connect(from, to) match {
      case Failure(ex) => Left(PatchingException(ex))
      case Success(()) =>
        val id = PatchCableId()
        val cable = PatchCable(from, to)
        cables.put(id, (name.getOrElse(id.toString), cable))
        Right(id)
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

}

object PatchPanel {

  final case class PatchCable(
    from: AutoCloseable,
    to: AutoCloseable
  )

}