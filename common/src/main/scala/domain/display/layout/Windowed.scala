package domain.display.layout

import domain.math.temporal.Window

final case class Windowed[A](window: Window, glyph: A)
