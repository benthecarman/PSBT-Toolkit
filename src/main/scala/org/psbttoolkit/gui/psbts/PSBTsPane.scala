package org.psbttoolkit.gui.psbts

import org.psbttoolkit.gui.TaskRunner
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, Separator, TextArea}
import scalafx.scene.layout.{BorderPane, HBox, VBox}

class PSBTsPane(glassPane: VBox) {

  private val resultArea = new TextArea {
    editable = true
    wrapText = true
    promptText = "PSBT"
    text = ""
  }

  val model = new PSBTsPaneModel(resultArea)

  val psbtButtons = new PSBTButtons(model)

  val globalCol: VBox = new VBox {
    spacing = 10
    children = Vector(new Label("Global Functions"),
                      new Separator) ++ psbtButtons.globalButtons
  }

  val inputCol: VBox = new VBox {
    spacing = 10
    children = Vector(new Label("Input Functions"),
                      new Separator) ++ psbtButtons.inputButtons
  }

  val outputCol: VBox = new VBox {
    spacing = 10
    children = Vector(new Label("Output Functions"),
                      new Separator) ++ psbtButtons.outputButtons
  }

  val generalCol: VBox = new VBox {
    spacing = 10
    children = Vector(new Label("General Functions"),
                      new Separator) ++ psbtButtons.generalButtons
  }

  val buttonPane: HBox = new HBox {
    spacing = 10
    children = Vector(globalCol, inputCol, outputCol, generalCol)
  }

  val view: BorderPane = new BorderPane {
    padding = Insets(top = 20, right = 10, bottom = 10, left = 10)

    center = buttonPane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)

  private val taskRunner = new TaskRunner(view, glassPane)
  model.taskRunner = taskRunner
}
