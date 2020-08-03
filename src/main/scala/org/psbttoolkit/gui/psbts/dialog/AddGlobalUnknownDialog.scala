package org.psbttoolkit.gui.psbts.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window
import scodec.bits.ByteVector

object AddGlobalUnknownDialog {

  def showAndWait(parentWindow: Window): Option[(ByteVector, ByteVector)] = {

    val dialog = new Dialog[Option[(ByteVector, ByteVector)]]() {
      initOwner(parentWindow)
      title = s"Add Global Unknown Record"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val keyTF = new TextField() {
      promptText = "Serialized in hex"
    }
    val dataTF = new TextField() {
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

      addRow("Key", keyTF)
      addRow("Data", dataTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== keyTF.text.isEmpty || dataTF.text.isEmpty

    Platform.runLater(keyTF.requestFocus())

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some(
          (ByteVector.fromValidHex(keyTF.text.value),
           ByteVector.fromValidHex(dataTF.text.value)))
      } else None

    dialog.showAndWait() match {
      case Some(Some((key: ByteVector, data: ByteVector))) =>
        Some((key, data))
      case Some(_) | None => None
    }
  }
}
