package storage.file

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import core.components.Storage

import scala.collection.JavaConverters._

class SimpleFileStorage[A](name: String)(implicit serializer: Serializer[A]) extends Storage[A] {

  private val path: Path = Paths.get(new File(".").getCanonicalPath,  "data",  name + ".dat")

  override def put(thing: A): Unit = {
    Files.write(
      path,
      serializer.serialize(thing).getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND
    )
  }

  override def getAll: Vector[A] = {
    if (Files.exists(path)) Files.readAllLines(path, StandardCharsets.UTF_8).asScala.map(serializer.deserialize).toVector
    else Vector[A]()
  }
}
