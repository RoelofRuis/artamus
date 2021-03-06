package server.rendering

import net.codingwell.scalaguice.ScalaPrivateModule
import server.rendering.impl.{LilypondRenderer, LilypondCommandLineExecutor, LilypondInterpreter}

class RenderingModule extends ScalaPrivateModule {
  this: RenderingConfig =>

  override def configure(): Unit = {
    bind[LilypondInterpreter].toInstance(
      new LilypondInterpreter(
        lyVersion,
        paperSize
      )
    )

    bind[LilypondCommandLineExecutor].toInstance(
      new LilypondCommandLineExecutor(
        resourceRootPath,
        cleanupLySources,
        pngResolution,
      ))
    bind[Renderer].to[LilypondRenderer].asEagerSingleton()

    expose[Renderer]
  }

}

