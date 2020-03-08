package storage.impl.file

import java.nio.file.{Path, Paths}

final case class DbPath(root: String) {

  def toTable(table: String): Path = Paths.get(root, table)
  def toRow(table: String, row: String, deletedVersion: Option[Int] = None): Path = {
    val rowName = if (deletedVersion.isDefined) s"${row}_${deletedVersion.get.toString}" else row
    Paths.get(root, table, rowName)
  }
  def toObject(table: String, row: String, extension: String, version: Int): Path = {
    Paths.get(root, table, row, DbPath.objectFileName(version, extension))
  }

}

object DbPath {
  def toObjectFromRowPath(rowPath: Path, version: Int, extension: String): Path = {
    Paths.get(rowPath.toString, DbPath.objectFileName(version, extension))
  }
  def objectFileName(version: Int, extension: String): String = {
    s"${version.toString}.${extension}"
  }
}
