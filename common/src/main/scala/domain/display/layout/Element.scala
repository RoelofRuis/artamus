package domain.display.layout

import domain.math.temporal.Window

final case class Element[A](window: Window, glyph: A)
