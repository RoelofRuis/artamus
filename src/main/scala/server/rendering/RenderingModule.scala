package server.rendering

import net.codingwell.scalaguice.ScalaPrivateModule
import server.rendering.interpret.LilypondInterpreter
import server.rendering.service.{AsyncRenderingBank, LilypondCommandLineExecutor, TrackRenderer}

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
    bind[AsyncRenderingBank].asEagerSingleton()
    bind[Renderer].to[TrackRenderer].asEagerSingleton()

    expose[Renderer]
  }

}

