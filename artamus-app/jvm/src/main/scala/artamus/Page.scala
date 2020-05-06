package artamus

import scalatags.Text.all.{link, _}

object Page {
  val boot = "artamus.Client().main(document.getElementById('contents'))"

  val skeleton =
    html(
      head(
        script(src:="/artamus-app-fastopt.js"),
        link(
          rel := "stylesheet",
          href :="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        )
      ),
      body(
        onload:=boot,
        div(id:="contents")
      )
    )
}
