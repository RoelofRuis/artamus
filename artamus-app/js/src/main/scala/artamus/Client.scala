package artamus

import org.scalajs.dom.html

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("artamus")
object Client {

  @JSExport
  def main(container: html.Div): Unit = {
    container.blur()
  }

}
