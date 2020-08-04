package org.psbttoolkit.gui.crypto.dialog

import org.bitcoins.crypto.{ECPrivateKey, SchnorrPublicKey}
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object ConvertToSchnorrPublicKeyDialog {

  def showAndWait(parentWindow: Window): Option[SchnorrPublicKey] = {
    val dialog = new Dialog[Option[SchnorrPublicKey]]() {
      initOwner(parentWindow)
      title = "Convert to Schnorr Public Key"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val privKeyTF = new TextField() {
      promptText = "Hex Encoded"
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
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== privKeyTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val keyStr = privKeyTF.text.value
        val key = ECPrivateKey(keyStr)

        Some(key.schnorrPublicKey)
      } else None

    dialog.showAndWait() match {
      case Some(Some(pubkey: SchnorrPublicKey)) =>
        Some(pubkey)
      case Some(_) | None =>
        None
    }
  }
}
