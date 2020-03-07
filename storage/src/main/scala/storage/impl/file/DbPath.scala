package storage.impl.file

import java.nio.file.{Path, Paths}

import storage.api.DataTypes.{DataType, JSON, Raw}

final case class DbPath(root: String) {

  def toTable(table: String): Path = Paths.get(root, table)
  def toRow(table: String, row: String, deletedVersion: Option[Int] = None): Path = {
    val rowName = if (deletedVersion.isDefined) s"${row}_${deletedVersion.get.toString}" else row
    Paths.get(root, table, rowName)
  }
  def toObject(table: String, row: String, version: Int, dataType: DataType): Path = {
    Paths.get(root, table, row, DbPath.objectFileName(version, dataType))
  }

}

object DbPath {
  def toObjectFromRowPath(rowPath: Path, version: Int, dataType: DataType): Path = {
    Paths.get(rowPath.toString, DbPath.objectFileName(version, dataType))
  }
  def objectFileName(version: Int, dataType: DataType): String = {
    val extension = dataType match {
      case JSON => "json"
      case Raw => "dat"
    }
    s"${version.toString}.$extension"
  }
}
