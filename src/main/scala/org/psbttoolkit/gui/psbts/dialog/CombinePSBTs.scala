package org.psbttoolkit.gui.psbts.dialog

import org.bitcoins.core.psbt.PSBT
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object CombinePSBTs {

  def showAndWait(parentWindow: Window): Option[PSBT] = {
    val dialog = new Dialog[Option[PSBT]]() {
      initOwner(parentWindow)
      title = "Combine PSBTs"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val psbtTA = new TextArea() {
      wrapText = true
      promptText = "Serialized in hex or base64"
    }

    dialog.dialogPane().content = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      var nextRow: Int = 0

      def addRow(label: String, textArea: TextArea): Unit = {
        add(new Label(label), 0, nextRow)
        add(textArea, 1, nextRow)
        nextRow += 1
      }

      addRow("PSBT", psbtTA)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== psbtTA.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some(PSBT.fromString(psbtTA.text.value))
      } else None

    dialog.showAndWait() match {
      case Some(Some(psbt: PSBT)) => Some(psbt)
      case Some(_) | None         => None
    }
  }
}
