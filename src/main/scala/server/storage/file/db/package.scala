package server.storage.file

import java.io.File

package object db {

  final case class DataFile(obj: String, ext: String)

  trait Paths {
    val file: DataFile
    val rootPath: Seq[String]
    val version: Option[Long]

    lazy val dir: String = (rootPath :+ file.obj).mkString(File.separator)
    lazy val latestPath: String = path(version)
    def path(version: Option[Long]): String = {
      val versionString = version.map(s => s"-$s").getOrElse("")
      val dataFile = s"data$versionString.${file.ext}"
      (rootPath :+ file.obj :+ dataFile).mkString(File.separator)
    }
  }

  final case class Write(file: DataFile, rootPath: Seq[String], version: Option[Long], data: String) extends Paths

  final case class Read(file: DataFile, version: Option[Long], rootPath: Seq[String]) extends Paths

}
