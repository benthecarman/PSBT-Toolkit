package org.psbttoolkit.gui.crypto

import org.psbttoolkit.gui.TaskRunner
import scalafx.geometry.Insets
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{BorderPane, TilePane, VBox}

class CryptoPane(glassPane: VBox) {

  private val resultArea = new TextArea {
    editable = true
    wrapText = true
    promptText = ""
    text = ""
  }

  val model = new CryptoPaneModel(resultArea)

  val buttons = new CryptoButtons(model)

  private val buttonPane = new TilePane {
    hgap = 10
    vgap = 10
    children = buttons.all
  }

  val view: BorderPane = new BorderPane {
    padding = Insets(top = 20, right = 10, bottom = 10, left = 10)

    center = buttonPane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)

  buttons.setMinWidth()

  private val taskRunner = new TaskRunner(buttonPane, glassPane)
  model.taskRunner = taskRunner
}
