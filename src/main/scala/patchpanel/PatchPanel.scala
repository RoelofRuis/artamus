package patchpanel

import java.util.concurrent.CopyOnWriteArrayList

import javax.inject.Inject
import patchpanel.PatchPanel.{PatchCable, PatchingException}

import scala.util.{Failure, Success}

class PatchPanel @Inject() () {

  private val cables = new CopyOnWriteArrayList[PatchCable]()

  def plugIn[T <: AutoCloseable, R <: AutoCloseable](
    transmitter: TransmitterDevice[T],
    receiver: ReceiverDevice[R]
  )(implicit connector: CanConnect[T, R]): Seq[PatchingException] = {
    transmitter.newTransmitterJack match {
      case Failure(outputException) => Seq(PatchingException(outputException))
      case Success(transmitterJack) =>
        receiver.newReceiverJack match {
          case f @ Failure(_) =>
            Seq(
              f,
              transmitterJack.close()
            ).collect { case Failure(ex) => PatchingException(ex) }
          case Success(receiverJack) =>
            connector.connect(transmitterJack, receiverJack) match {
              case Success(()) =>
                cables.add(PatchCable(
                  transmitter.deviceId,
                  receiver.deviceId,
                  transmitterJack,
                  receiverJack
                ))
                Seq()
              case f @ Failure(_) =>
                Seq(
                  f,
                  transmitterJack.close(),
                  receiverJack.close()
                ).collect{ case Failure(ex) => PatchingException(ex) }
            }
        }
    }
  }

}

object PatchPanel {

  final case class PatchCable(
    transmitterDeviceId: DeviceId,
    receiverDeviceId: DeviceId,
    transmitterJack: AutoCloseable,
    receiverJack: AutoCloseable
  )

  final case class PatchingException(cause: Throwable)

}