package org.psbttoolkit.gui.psbts.dialog

import org.bitcoins.core.script.crypto.HashType
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object AddSigHashTypeDialog {

  def showAndWait(parentWindow: Window): Option[(Int, HashType)] = {
    val dialog = new Dialog[Option[(Int, HashType)]]() {
      initOwner(parentWindow)
      title = "Add SigHash Type"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val hashTypes = HashType.hashTypes

    val indexTF = new TextField()
    val sigHashCB = new ComboBox(hashTypes)

    dialog.dialogPane().content = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      var nextRow: Int = 0

      def addRow(label: String, node: Node): Unit = {
        add(new Label(label), 0, nextRow)
        add(node, 1, nextRow)
        nextRow += 1
      }

      addRow("Input Index", indexTF)
      addRow("Sig Hash Type", sigHashCB)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== indexTF.text.isEmpty

    Platform.runLater(indexTF.requestFocus())

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        Some(
          (indexTF.text.value.toInt,
           sigHashCB.value.value.asInstanceOf[HashType]))
      } else None

    dialog.showAndWait() match {
      case Some(Some((index: Int, hashType: HashType))) =>
        Some((index, hashType))
      case Some(_) | None => None
    }
  }
}
