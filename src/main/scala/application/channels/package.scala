package application

import application.model.Track

package object channels {

  trait ChannelType {
    type A
  }

  case object Playback extends ChannelType { type A = Track }
  case object Logging extends ChannelType { type A = String }

}
