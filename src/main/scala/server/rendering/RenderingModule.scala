package server.rendering

import net.codingwell.scalaguice.ScalaPrivateModule

class RenderingModule extends ScalaPrivateModule {
  this: RenderingConfig =>

  override def configure(): Unit = {
    bind[LilypondCommandLineExecutor].toInstance(
      new LilypondCommandLineExecutor(
        resourceRootPath,
        cleanupLySources,
        pngResolution,
      ))
    bind[Renderer].to[AsyncRenderer].asEagerSingleton()

    expose[Renderer]
  }

}

