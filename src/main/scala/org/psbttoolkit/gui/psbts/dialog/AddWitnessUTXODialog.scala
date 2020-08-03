package org.psbttoolkit.gui.psbts.dialog

import org.bitcoins.core.protocol.transaction.TransactionOutput
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object AddWitnessUTXODialog {

  def showAndWait(parentWindow: Window): Option[(Int, TransactionOutput)] = {
    val dialog = new Dialog[Option[(Int, TransactionOutput)]]() {
      initOwner(parentWindow)
      title = "Add Witness UTXO"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val indexTF = new TextField()
    val outputTF = new TextField() {
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
      addRow("Output", outputTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== indexTF.text.isEmpty || outputTF.text.isEmpty

    Platform.runLater(indexTF.requestFocus())

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some((indexTF.text.value.toInt, TransactionOutput(outputTF.text.value)))
      } else None

    dialog.showAndWait() match {
      case Some(Some((index: Int, output: TransactionOutput))) =>
        Some((index, output))
      case Some(_) | None => None
    }
  }
}
