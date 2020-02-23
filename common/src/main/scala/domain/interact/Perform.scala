package domain.interact

import domain.perform.TrackPerformance

object Perform {

  final case object PreparePerformance extends Query { type Res = TrackPerformance }

}
