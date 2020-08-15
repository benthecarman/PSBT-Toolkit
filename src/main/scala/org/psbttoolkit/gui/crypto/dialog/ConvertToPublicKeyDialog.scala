package org.psbttoolkit.gui.crypto.dialog

import org.bitcoins.crypto.{ECPrivateKey, NetworkElement}
import org.psbttoolkit.gui.GlobalData
import org.psbttoolkit.gui.crypto.types.PublicKeyEncoding._
import org.psbttoolkit.gui.crypto.types.PublicKeyEncoding
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object ConvertToPublicKeyDialog {

  def showAndWait(parentWindow: Window): Option[NetworkElement] = {
    val dialog = new Dialog[Option[NetworkElement]]() {
      initOwner(parentWindow)
      title = "Convert to Public Key"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val privKeyTF = new TextField() {
      promptText = "Hex Encoded"
    }

    var pubKeyEncoding: PublicKeyEncoding = ECDSA

    val pubKeyEncodingSelector: ComboBox[String] = new ComboBox(
      PublicKeyEncoding.names) {
      value = ECDSA.toString

      onAction = (_: ActionEvent) => {
        pubKeyEncoding = PublicKeyEncoding.fromString(value.value)
      }
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Private Key"), 0, 0)
      add(privKeyTF, 1, 0)
      add(pubKeyEncodingSelector, 2, 0)
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

        pubKeyEncoding match {
          case ECDSA =>
            Some(key.publicKey)
          case Schnorr =>
            Some(key.schnorrPublicKey)
        }
      } else None

    dialog.showAndWait() match {
      case Some(Some(pubkey: NetworkElement)) =>
        Some(pubkey)
      case Some(_) | None =>
        None
    }
  }
}
