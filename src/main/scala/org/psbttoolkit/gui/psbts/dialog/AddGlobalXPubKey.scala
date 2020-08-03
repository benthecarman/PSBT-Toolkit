package org.psbttoolkit.gui.psbts.dialog

import org.bitcoins.core.crypto.ExtPublicKey
import org.bitcoins.core.hd.BIP32Path
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window
import scodec.bits.ByteVector

import scala.util.{Failure, Success, Try}

object AddGlobalXPubKey {

  def showAndWait(
      parentWindow: Window): Option[(ExtPublicKey, ByteVector, BIP32Path)] = {
    val dialog = new Dialog[Option[(ExtPublicKey, ByteVector, BIP32Path)]]() {
      initOwner(parentWindow)
      title = "Add XPub Key"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val extPubKeyTF = new TextField()
    val fingerprintTF = new TextField()
    val pathTF = new TextField()

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

      addRow("XPub Key", extPubKeyTF)
      addRow("Master Fingerprint", fingerprintTF)
      addRow("BIP 32 Path", pathTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== extPubKeyTF.text.isEmpty || fingerprintTF.text.isEmpty || pathTF.text.isEmpty

    Platform.runLater(extPubKeyTF.requestFocus())

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some(getExtKey(extPubKeyTF.text.value).get,
             ByteVector.fromValidHex(fingerprintTF.text.value),
             getBIP32Path(pathTF.text.value).get)
      } else None

    dialog.showAndWait() match {
      case Some(
            Some(
              (extPubKey: ExtPublicKey,
               fingerprint: ByteVector,
               path: BIP32Path))) =>
        Some(extPubKey, fingerprint, path)
      case Some(_) | None => None
    }
  }

  private def getExtKey(str: String): Try[ExtPublicKey] = {
    ExtPublicKey.fromString(str) match {
      case Success(key) => Success(key)
      case Failure(_) =>
        Try(ExtPublicKey.fromHex(str))
    }
  }

  private def getBIP32Path(str: String): Try[BIP32Path] = {
    Try(BIP32Path.fromString(str)) match {
      case Success(key) => Success(key)
      case Failure(_) =>
        Try(BIP32Path.fromHex(str))
    }
  }
}
