package org.psbttoolkit.gui.crypto.dialog

import org.bitcoins.crypto.{ECPrivateKey, ECPublicKey, NetworkElement}
import org.psbttoolkit.gui.GlobalData
import org.psbttoolkit.gui.crypto.SignatureSchema
import org.psbttoolkit.gui.crypto.SignatureSchema._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window
import scodec.bits.ByteVector

import scala.concurrent.ExecutionContext.global

object SignDataDialog {

  def showAndWait(parentWindow: Window): Option[NetworkElement] = {
    val dialog = new Dialog[Option[NetworkElement]]() {
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

    val adaptorPointLabel = new Label("Adaptor Point")

    val adaptorPointTF = new TextField() {
      promptText = "32 bytes (hex encoded)"
    }

    def setAdaptorPointVisibility(visibility: Boolean): Unit = {
      adaptorPointLabel.setVisible(visibility)
      adaptorPointTF.setVisible(visibility)
    }

    var signSchema: SignatureSchema = ECDSA

    val signSelector: ComboBox[String] = new ComboBox(SignatureSchema.names) {
      value = SignatureSchema.ECDSA.toString

      onAction = (_: ActionEvent) => {
        signSchema = SignatureSchema.fromString(value.value)
        signSchema match {
          case ECDSA | Schnorr =>
            setAdaptorPointVisibility(false)
          case AdaptorSign =>
            setAdaptorPointVisibility(true)
        }
      }
    }

    val grid = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Private Key"), 0, 0)
      add(privKeyTF, 1, 0)
      add(signSelector, 2, 0)

      add(new Label("Data"), 0, 1)
      add(dataTF, 1, 1)

      add(adaptorPointLabel, 0, 2)
      add(adaptorPointTF, 1, 2)
    }

    setAdaptorPointVisibility(false)

    dialog.dialogPane().content = grid

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

        signSchema match {
          case ECDSA =>
            Some(key.signLowR(bytes)(global))
          case Schnorr =>
            Some(key.schnorrSign(bytes))
          case AdaptorSign =>
            val adaptorPointStr = adaptorPointTF.text.value
            val bytes = ByteVector.fromHex(adaptorPointStr) match {
              case Some(valid) => valid
              case None =>
                throw new IllegalArgumentException(
                  "Adaptor Point given was not correctly hex encoded")
            }
            val pubKey = ECPublicKey(bytes)
            Some(key.adaptorSign(pubKey, bytes))
        }
      } else None

    dialog.showAndWait() match {
      case Some(Some(sig: NetworkElement)) =>
        Some(sig)
      case Some(_) | None =>
        None
    }
  }
}
