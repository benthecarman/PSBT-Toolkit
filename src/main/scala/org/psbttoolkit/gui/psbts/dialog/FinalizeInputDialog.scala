package org.psbttoolkit.gui.psbts.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object FinalizeInputDialog {

  def showAndWait(parentWindow: Window): Option[Int] = {
    val dialog = new Dialog[Option[Int]]() {
      initOwner(parentWindow)
      title = "Finalize Input"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val indexTF = new TextField()

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

      addRow("Input Index", indexTF)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== indexTF.text.isEmpty

    Platform.runLater(indexTF.requestFocus())

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some(indexTF.text.value.toInt)
      } else None

    dialog.showAndWait() match {
      case Some(Some(version: Int)) => Some(version)
      case Some(_) | None           => None
    }
  }
}
