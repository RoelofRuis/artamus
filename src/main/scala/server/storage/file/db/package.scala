package server.storage.file

import java.io.File

package object db {

  final case class DataFile(obj: String, id: Option[Long], ext: String)

  trait Paths {
    val file: DataFile
    val rootPath: Seq[String]
    val version: Long

    lazy val dir: String = (rootPath :+ file.obj).mkString(File.separator)
    lazy val latestPath: String = path(version)
    def path(version: Long): String = {
      val dataFile = file.id match {
        case Some(id) => s"$id-$version.${file.ext}"
        case None => s"data-$version.${file.ext}"
      }
      (rootPath :+ file.obj :+ dataFile).mkString(File.separator)
    }
  }

  final case class Write(file: DataFile, rootPath: Seq[String], version: Long, data: String) extends Paths

  final case class Read(file: DataFile, version: Long, rootPath: Seq[String]) extends Paths

}
