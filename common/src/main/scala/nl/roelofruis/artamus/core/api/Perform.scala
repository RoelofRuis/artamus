package nl.roelofruis.artamus.core.api

import nl.roelofruis.artamus.core.model.performance.Performance

object Perform {

  final case object PreparePerformance extends Query { type Res = Performance }

}
