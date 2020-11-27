package org.psbttoolkit.gui.psbts

import org.psbttoolkit.gui.TaskRunner
import scalafx.geometry.{Insets, Pos}
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
    alignment = Pos.TopCenter
    spacing = 10
    children = Vector(new Label("Global Functions"),
                      new Separator) ++ psbtButtons.globalButtons
  }

  val inputCol: VBox = new VBox {
    alignment = Pos.TopCenter
    spacing = 10
    children = Vector(new Label("Input Functions"),
                      new Separator) ++ psbtButtons.inputButtons
  }

  val outputCol: VBox = new VBox {
    alignment = Pos.TopCenter
    spacing = 10
    children = Vector(new Label("Output Functions"),
                      new Separator) ++ psbtButtons.outputButtons
  }

  val generalCol: HBox = new HBox {
    alignment = Pos.BottomCenter
    spacing = 10
    children = psbtButtons.generalButtons
  }

  val general2Col: HBox = new HBox {
    alignment = Pos.BottomCenter
    spacing = 10
    children = psbtButtons.general2Buttons
  }

  val buttonPane: HBox = new HBox {
    alignment = Pos.Center
    spacing = 10
    children = Vector(globalCol, inputCol, outputCol)
  }

  val pane: BorderPane = new BorderPane {
    padding = Insets(top = 20, right = 10, bottom = 10, left = 10)

    center = buttonPane
    bottom = new VBox(generalCol, general2Col) {
      spacing = 10
    }
  }

  val view: BorderPane = new BorderPane {
    padding = Insets(top = 20, right = 10, bottom = 10, left = 10)

    center = pane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)

  psbtButtons.setMinWidth()

  private val taskRunner = new TaskRunner(view, glassPane)
  model.taskRunner = taskRunner
}
