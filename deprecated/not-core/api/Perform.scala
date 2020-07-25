package artamus.core.api

import artamus.core.model.performance.Performance

object Perform {

  final case object PreparePerformance extends Query { type Res = Performance }

}
