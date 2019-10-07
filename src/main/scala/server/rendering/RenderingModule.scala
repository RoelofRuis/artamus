package server.rendering

import net.codingwell.scalaguice.ScalaPrivateModule
import server.rendering.interpret.LilypondInterpreter
import server.rendering.render.{AsyncRenderingBank, LilypondCommandLineExecutor, TrackRenderer}

class RenderingModule extends ScalaPrivateModule {
  this: RenderingConfig =>

  override def configure(): Unit = {
    bind[LilypondInterpreter].asEagerSingleton()
    bind[LilypondCommandLineExecutor].toInstance(new LilypondCommandLineExecutor(resourceRootPath, cleanupLySources))
    bind[AsyncRenderingBank].asEagerSingleton()
    bind[Renderer].to[TrackRenderer].asEagerSingleton()

    expose[Renderer]
  }

}

