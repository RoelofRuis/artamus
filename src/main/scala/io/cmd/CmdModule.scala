package io.cmd

import core.components.AppRunner
import io.cmd.command._
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class CmdModule extends ScalaModule {

  override def configure(): Unit = {
    bind[AppRunner].to[CommandRunner]
    bind[Prompt].to[CommandLinePrompt]

    val commands = ScalaMultibinder.newSetBinder[Command](binder)
    commands.addBinding.to[IdeaCommand]
    commands.addBinding.to[QuitCommand]
    commands.addBinding.to[ListIdeasCommand]
  }

}
