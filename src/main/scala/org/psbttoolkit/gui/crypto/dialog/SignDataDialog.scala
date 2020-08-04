package org.psbttoolkit.gui.crypto.dialog

import org.bitcoins.crypto.{ECDigitalSignature, ECPrivateKey}
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window
import scodec.bits.ByteVector

import scala.concurrent.ExecutionContext.global

object SignDataDialog {

  def showAndWait(parentWindow: Window): Option[ECDigitalSignature] = {
    val dialog = new Dialog[Option[ECDigitalSignature]]() {
      initOwner(parentWindow)
      title = "Sign Data"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val privKeyTF = new TextField() {
      promptText = "Hex Encoded"
    }

    val dataTF = new TextField() {
      promptText = "32 bytes (hex encoded)"
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      var nextRow: Int = 0

      def addRow(label: String, textField: TextField): Unit = {
        add(new Label(label), 0, nextRow)
        add(textField, 1, nextRow)
        nextRow += 1
      }

      addRow("Private Key", privKeyTF)
      addRow("Data", dataTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== dataTF.text.isEmpty || privKeyTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val dataStr = dataTF.text.value
        val bytes = ByteVector.fromHex(dataStr) match {
          case Some(valid) => valid
          case None =>
            throw new IllegalArgumentException(
              "Data given was not correctly hex encoded")
        }
        require(bytes.size == 32,
                s"Data given was the incorrect size, got ${bytes.size}")

        val keyStr = privKeyTF.text.value
        val key = ECPrivateKey(keyStr)

        Some(key.signLowR(bytes)(global))
      } else None

    dialog.showAndWait() match {
      case Some(Some(sig: ECDigitalSignature)) =>
        Some(sig)
      case Some(_) | None =>
        None
    }
  }
}
