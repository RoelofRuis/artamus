import ApplicationBus.{Command, Handler}

import scala.util.{Success, Try}
import scala.language.higherKinds

val bus = new ApplicationBus()

case class MyCommand(input: Int) extends Command {
  type Res = Int
}

class MyCommandHandler extends Handler[MyCommand] {
  def handle(c: MyCommand): Try[Int] = {
    Success(c.input * 10)
  }
}

bus.registerHandler(new MyCommandHandler)
bus.execute(MyCommand(42))