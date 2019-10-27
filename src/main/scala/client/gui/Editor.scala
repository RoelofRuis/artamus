package client.gui

class Editor {

  val frame = new EditorFrame with EditorBindings

  frame.pack
  frame.visible=true

}
