package org.psbttoolkit.gui.crypto.dialog

import javafx.event.ActionEvent
import org.bitcoins.crypto.NetworkElement
import org.psbttoolkit.gui.GlobalData
import org.psbttoolkit.gui.crypto.types.HashAlgo
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, ComboBox, Dialog, TextField}
import scalafx.scene.layout.HBox
import scalafx.stage.Window
import scodec.bits.ByteVector

object HashDataDialog {

  def showAndWait(parentWindow: Window): Option[NetworkElement] = {
    val dialog = new Dialog[Option[NetworkElement]]() {
      initOwner(parentWindow)
      title = "Hash Data"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val dataTF = new TextField() {
      promptText = "Data to Hash"
    }

    var hashAlgo: HashAlgo = HashAlgo.SHA256

    val hashTypeSelector: ComboBox[String] = new ComboBox(HashAlgo.names) {
      value = HashAlgo.SHA256.toString

      onAction = (_: ActionEvent) => {
        hashAlgo = HashAlgo.fromString(value.value)
      }
    }

    dialog.dialogPane().content = new HBox() {
      spacing = 10
      padding = Insets(20, 10, 10, 10)

      children = Vector(dataTF, hashTypeSelector)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== dataTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val str = dataTF.text.value

        val toHash = ByteVector.fromHex(str) match {
          case Some(valid) =>
            valid
          case None =>
            ByteVector(str.getBytes)
        }

        val result = hashAlgo.hash(toHash)

        Some(result)
      } else None

    dialog.showAndWait() match {
      case Some(Some(ne: NetworkElement)) =>
        Some(ne)
      case Some(_) | None =>
        None
    }
  }
}
