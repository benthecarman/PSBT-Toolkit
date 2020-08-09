package org.psbttoolkit.gui.crypto.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, TextField}
import scalafx.scene.layout.HBox
import scalafx.stage.Window
import scodec.bits.ByteVector

object FlipEndiannessDialog {

  def showAndWait(parentWindow: Window): Option[ByteVector] = {
    val dialog = new Dialog[Option[ByteVector]]() {
      initOwner(parentWindow)
      title = "Flip Endianness"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val dataTF = new TextField() {
      promptText = "hex encoded bytes"
    }

    dialog.dialogPane().content = new HBox() {
      spacing = 10
      padding = Insets(20, 10, 10, 10)

      children = Vector(dataTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== dataTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val str = dataTF.text.value

        val bytes = ByteVector.fromValidHex(str)

        Some(bytes.reverse)
      } else None

    dialog.showAndWait() match {
      case Some(Some(bytes: ByteVector)) =>
        Some(bytes)
      case Some(_) | None =>
        None
    }
  }
}
