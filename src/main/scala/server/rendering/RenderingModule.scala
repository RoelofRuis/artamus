package server.rendering

import net.codingwell.scalaguice.ScalaPrivateModule
import server.rendering.service.{AsyncRenderer, LilypondCommandLineExecutor}

class RenderingModule extends ScalaPrivateModule {
  this: RenderingConfig =>

  override def configure(): Unit = {
    bind[LilypondCommandLineExecutor].toInstance(
      new LilypondCommandLineExecutor(
        resourceRootPath,
        cleanupLySources,
        pngResolution,
      ))
    bind[AsyncRenderer].asEagerSingleton()

    expose[Renderer]
  }

}

