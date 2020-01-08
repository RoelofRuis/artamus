import java.util.UUID

import scala.util.{Failure, Try}

package object patchpanel {

  type PatchId = UUID
  type DeviceId = String

  final case class PatchCableId(id: UUID = UUID.randomUUID())

  final case class PatchingException(causes: Throwable*) extends Throwable

  object PatchingException {

    def withCleanup(cause: Throwable, additional: Seq[Try[Unit]] = Seq()): PatchingException = {
      PatchingException(cause +: additional.collect { case Failure(ex) => ex }: _*)
    }

  }

}
