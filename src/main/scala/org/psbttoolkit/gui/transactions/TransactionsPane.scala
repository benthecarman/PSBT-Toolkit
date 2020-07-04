package org.psbttoolkit.gui.transactions

import scalafx.scene.control.TextArea
import scalafx.scene.layout.StackPane

class TransactionsPane {

  private val resultArea = new TextArea {
    editable = false
    wrapText = true
    text = "Transactions"
  }

  val view: StackPane = new StackPane {
    children = List(resultArea)
  }
}
