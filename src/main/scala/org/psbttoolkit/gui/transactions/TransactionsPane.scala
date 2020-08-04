package org.psbttoolkit.gui.transactions

import org.psbttoolkit.gui.TaskRunner
import scalafx.geometry.Insets
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{BorderPane, TilePane, VBox}

class TransactionsPane(glassPane: VBox) {

  private val resultArea = new TextArea {
    editable = true
    wrapText = true
    promptText = "Transaction (hex)"
    text = ""
  }

  val model = new TransactionsPaneModel(resultArea)

  val txButtons = new TransactionsButtons(model)

  private val buttonPane = new TilePane {
    hgap = 10
    children = txButtons.all
  }

  val view: BorderPane = new BorderPane {
    padding = Insets(top = 20, right = 10, bottom = 10, left = 10)

    center = buttonPane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)

  txButtons.setMinWidth()

  private val taskRunner = new TaskRunner(buttonPane, glassPane)
  model.taskRunner = taskRunner
}
