package server.storage.file

import java.io.File

package object db {

  final case class DataFile(obj: String, id: Option[Long], ext: String)

  final case class Write(file: DataFile, rootPath: Seq[String], data: String) {
    lazy val dir: String = (rootPath :+ file.obj).mkString(File.separator)

    lazy val path: String = {
      val dataFile = file.id match {
        case Some(id) => s"$id.${file.ext}"
        case None => s"data.${file.ext}"
      }
      (rootPath :+ file.obj :+ dataFile).mkString(File.separator)
    }
  }

  final case class Read(file: DataFile, rootPath: Seq[String]) {
    lazy val path: String = {
      val dataFile = file.id match {
        case Some(id) => s"$id.${file.ext}"
        case None => s"data.${file.ext}"
      }
      (rootPath :+ file.obj :+ dataFile).mkString(File.separator)
    }
  }

}
