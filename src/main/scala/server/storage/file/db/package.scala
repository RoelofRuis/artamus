package server.storage.file

package object db {

  final case class Write(dir: String, path: String, data: String)
  final case class Read(path: String)

}
