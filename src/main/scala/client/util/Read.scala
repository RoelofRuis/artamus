package client.util

import client.util.BlockingReader.ReadMethod

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object Read {

  // TODO: see if this is the correct place for the reader functions

  def untilEnter[A]: ReadMethod[A, List] = { queue =>
    System.in.read()
    val buffer = new ListBuffer[A]
    queue.drainTo(buffer.asJava)
    buffer.toList
  }

  def numElements[A](n: Int): ReadMethod[A, List] = { queue =>
    Range(0, n).map(_ => queue.take()).toList
  }

}
