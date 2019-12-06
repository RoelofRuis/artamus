package server.storage.file

package object db {

  final case class Write(path: String, data: String)
  final case class Read(path: String)

}
