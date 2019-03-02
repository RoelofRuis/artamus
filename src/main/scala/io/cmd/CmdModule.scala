package io.cmd

import io.cmd.command._
import core.app.AppRunner
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class CmdModule extends ScalaModule {

  override def configure(): Unit = {
    bind[Prompt].to[CommandLinePrompt]
    bind[AppRunner].to[CommandRunner]

    val commands = ScalaMultibinder.newSetBinder[Command](binder)
    commands.addBinding.to[IdeaCommand]
    commands.addBinding.to[QuitCommand]
    commands.addBinding.to[ListIdeasCommand]
  }

}
