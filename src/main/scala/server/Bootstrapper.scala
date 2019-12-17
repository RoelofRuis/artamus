package server

import java.util.UUID

import javax.inject.Inject
import music.domain.write.user.User
import music.domain.write.user.User.UserId
import protocol.ServerInterface
import server.rendering.Renderer
import storage.api.DbWithRead
import server.model.Users._

class Bootstrapper @Inject() (
  server: ServerInterface,
  renderer: Renderer,
  db: DbWithRead
) {

  def run(): Unit = {
    createDefaultUser()

    println("Starting server...")

    server.accept()
    renderer.shutdown()

    println("\nProgram ended")
  }

  private def createDefaultUser(): Unit = {
    val transaction = db.newTransaction
    transaction.saveUser(User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000000")), "artamus"))
    transaction.commit()
  }
}
