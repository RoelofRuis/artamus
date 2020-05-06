package artamus

object Server extends cask.MainRoutes {
  @cask.get("/")
  def hello() = Page.skeleton.render

  initialize()
}