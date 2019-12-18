package server

import java.util.UUID

import javax.inject.Inject
import music.model.write.user.User
import music.model.write.user.User.UserId
import protocol.ServerInterface
import server.model.Users._
import storage.api.DbWithRead

class Bootstrapper @Inject() (
  server: ServerInterface,
  db: DbWithRead
) {

  def run(): Unit = {
    createDefaultUser()

    println("Starting server...")

    server.accept()

    println("\nProgram ended")
  }

  private def createDefaultUser(): Unit = {
    val transaction = db.newTransaction
    transaction.saveUser(User(UserId(UUID.fromString("00000000-0000-0000-0000-000000000000")), "artamus"))
    transaction.commit()
  }
}
