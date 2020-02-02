package domain.record

import domain.math.Rational

/**
 * Represents the static settings of a quantizer.
 *
 * @param instantaneousThreshold The millisecond threshold under which events would be perceived as occurring at the
 * same time. The default value is an assumption: based on 120 BPM a 64th note would be just above 31 ms inter onset
 * interval. 12 ms inter onset interval is the human limit. This, at least in theory, allows for measuring at least
 * 32th notes with reasonable precision.
 * @param interClusterDistance The maximum millisecond distance that time deltas might be considered to be part of the
 * same cluster. The default is put above the instantaneousThreshold but well below 250, the assumed duration of an
 * 8th note on 120 BPM.
 * @param wholeNoteDuration The number of milliseconds that a whole note takes. The default value is assuming 120 BPM.
 * @param consideredLengths The note lengths that will be considered by the algorithm.
 * @param debug Whether printing of debug information is allowed. // TODO: implement!
 */
final case class Quantizer(
  instantaneousThreshold: Int = 30,
  interClusterDistance: Int = 100,
  wholeNoteDuration: Int = 2000,
  consideredLengths: Set[Rational] = Set(
    Rational(1, 8), Rational(1, 4), Rational(3, 8), Rational(1, 2),
    Rational(5, 8), Rational(3, 4), Rational(7, 8), Rational(1, 2)
  ),
  debug: Boolean = false
)
