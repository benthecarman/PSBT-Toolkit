package org.psbttoolkit.gui.psbts

import org.psbttoolkit.gui.TaskRunner
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{BorderPane, TilePane, VBox}

class PSBTsPane(glassPane: VBox) {

  private val resultArea = new TextArea {
    editable = true
    wrapText = true
    promptText = "PSBT"
    text = ""
  }

  val model = new PSBTsPaneModel(resultArea)

  val psbtButtons = new PSBTButtons(model)

  private val buttonPane = new TilePane {
    children = psbtButtons.all
  }

  val view: BorderPane = new BorderPane {
    center = buttonPane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)

  private val taskRunner = new TaskRunner(buttonPane, glassPane)
  model.taskRunner = taskRunner
}
