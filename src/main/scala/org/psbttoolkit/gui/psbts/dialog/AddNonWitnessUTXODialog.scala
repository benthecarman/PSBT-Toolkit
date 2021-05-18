package org.psbttoolkit.gui.psbts.dialog

import org.bitcoins.core.protocol.transaction.Transaction
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object AddNonWitnessUTXODialog {

  def showAndWait(parentWindow: Window): Option[(Int, Transaction)] = {
    val dialog = new Dialog[Option[(Int, Transaction)]]() {
      initOwner(parentWindow)
      title = "Add Non-Witness UTXO"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val indexTF = new TextField()
    val transactionTF = new TextField() {
      promptText = "Serialized in hex"
    }

    dialog.dialogPane().content = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      var nextRow: Int = 0
      def addRow(label: String, textField: TextField): Unit = {
        add(new Label(label), 0, nextRow)
        add(textField, 1, nextRow)
        nextRow += 1
      }

      addRow("Input Index", indexTF)
      addRow("Transaction", transactionTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== indexTF.text.isEmpty || transactionTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some((indexTF.text.value.toInt, Transaction(transactionTF.text.value)))
      } else None

    dialog.showAndWait() match {
      case Some(Some((index: Int, tx: Transaction))) =>
        Some((index, tx))
      case Some(_) | None => None
    }
  }
}
