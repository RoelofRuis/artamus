package nl.roelofruis.patching

import scala.util.Try

trait CanConnect[O <: AutoCloseable, I <: AutoCloseable] {
  def connect(t: O, r: I): Try[Unit]
}
