package domain.primitives

/**
 * @param n The base duration, expressed as `1 / pow(2, n)`
 * @param dots The number of dots, each dots adds half of the previous note value to the duration.
 */
final case class NoteValue(n: Int, dots: Int)
