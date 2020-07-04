package org.psbttoolkit.gui.psbts

import scalafx.scene.control.TextArea
import scalafx.scene.layout.StackPane

class PSBTsPane {

  private val resultArea = new TextArea {
    editable = false
    wrapText = true
    text = "PSBTs"
  }

  val view: StackPane = new StackPane {
    children = List(resultArea)
  }
}
