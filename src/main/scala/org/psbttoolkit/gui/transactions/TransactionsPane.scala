package org.psbttoolkit.gui.transactions

import org.psbttoolkit.gui.TaskRunner
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Label, Separator, TextArea}
import scalafx.scene.layout.{BorderPane, TilePane, VBox}

class TransactionsPane(glassPane: VBox) {

  private val resultArea = new TextArea {
    editable = true
    wrapText = true
    promptText = "Hex encoded data"
    text = ""
  }

  val model = new TransactionsPaneModel(resultArea)

  val txButtons = new TransactionsButtons(model)

  private val txButtonPane = new TilePane {
    hgap = 10
    alignment = Pos.Center
    children = txButtons.txButtons
  }

  val separator1 = new Separator

  private val txButtonArea: VBox = new VBox() {
    padding = Insets(top = 0, right = 0, bottom = -10, left = 0)
    spacing = 10
    val txTitle = new Label("Raw Transactions")
    children = Vector(txTitle, txButtonPane)
    alignment = Pos.TopCenter
  }

  private val spkButtonPane = new TilePane {
    hgap = 10
    alignment = Pos.Center
    children = txButtons.spkButtons
  }

  val separator2 = new Separator

  private val spkButtonArea: VBox = new VBox() {
    padding = Insets(top = 20, right = 0, bottom = 0, left = 0)
    spacing = 10
    val spkTitle = new Label("Script Pub Keys")
    children = Vector(spkTitle, spkButtonPane, txButtons.createAddress)
    alignment = Pos.TopCenter
  }

  private val buttonBox = new VBox() {
    spacing = 0
    children = Vector(txButtonArea, separator1, spkButtonArea)
  }

  val view: BorderPane = new BorderPane {
    padding = Insets(top = 20, right = 10, bottom = 10, left = 10)

    center = buttonBox
    bottom = resultArea
  }

  buttonBox.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)

  txButtonArea.prefHeight <== buttonBox.height / 2
  spkButtonArea.prefHeight <== buttonBox.height / 2

  txButtons.setMinWidth()

  private val taskRunner = new TaskRunner(view, glassPane)
  model.taskRunner = taskRunner
}
