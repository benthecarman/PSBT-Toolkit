package org.psbttoolkit.gui.crypto.dialog

import org.bitcoins.crypto.{
  ECAdaptorSignature,
  ECDigitalSignature,
  ECPrivateKey
}
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window
import scodec.bits.ByteVector

object AdaptSignatureDialog {

  def showAndWait(parentWindow: Window): Option[ECDigitalSignature] = {
    val dialog = new Dialog[Option[ECDigitalSignature]]() {
      initOwner(parentWindow)
      title = "Adapt Signature"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val adaptedSignatureTF = new TextField() {
      promptText = "Adapted Signature"
    }

    val secretKeyTF = new TextField() {
      promptText = "Secret Key"
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Adapted Signature"), 0, 0)
      add(adaptedSignatureTF, 1, 0)

      add(new Label("Secret Key"), 0, 1)
      add(secretKeyTF, 1, 1)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== adaptedSignatureTF.text.isEmpty || secretKeyTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val adaptedSigStr = adaptedSignatureTF.text.value
        val sigBytes = ByteVector.fromValidHex(adaptedSigStr)
        val adaptedSig = ECAdaptorSignature(sigBytes)

        val secretKeyStr = secretKeyTF.text.value
        val secretKeyBytes = ByteVector.fromValidHex(secretKeyStr)
        val secretKey = ECPrivateKey(secretKeyBytes)

        Some(secretKey.completeAdaptorSignature(adaptedSig))
      } else None

    dialog.showAndWait() match {
      case Some(Some(sig: ECDigitalSignature)) =>
        Some(sig)
      case Some(_) | None =>
        None
    }
  }
}
