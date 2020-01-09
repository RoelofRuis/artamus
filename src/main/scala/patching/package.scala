import java.util.UUID

import scala.util.{Failure, Try}

package object patching {

  type PatchId = UUID
  type DeviceId = String

  final case class PatchCableId private (id: UUID)

  object PatchCableId {
    def apply(): PatchCableId = PatchCableId(UUID.randomUUID())
  }

  final case class PatchingException(causes: Throwable*) extends Throwable

  object PatchingException {

    def withCleanup(cause: Throwable, additional: Seq[Try[Unit]] = Seq()): PatchingException = {
      PatchingException(cause +: additional.collect { case Failure(ex) => ex }: _*)
    }

  }

}
