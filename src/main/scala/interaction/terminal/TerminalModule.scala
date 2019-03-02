package interaction.terminal

import core.components.AppRunner
import interaction.terminal.command._
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class TerminalModule extends ScalaModule {

  override def configure(): Unit = {
    bind[AppRunner].to[TerminalRunner]
    bind[Prompt].to[TerminalPrompt]

    val commands = ScalaMultibinder.newSetBinder[Command](binder)
    commands.addBinding.to[IdeaCommand]
    commands.addBinding.to[QuitCommand]
    commands.addBinding.to[ListIdeasCommand]
  }

}
