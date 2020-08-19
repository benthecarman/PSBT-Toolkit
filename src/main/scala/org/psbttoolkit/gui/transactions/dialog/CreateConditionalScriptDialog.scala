package org.psbttoolkit.gui.transactions.dialog

import org.bitcoins.core.protocol.script.{
  ConditionalScriptPubKey,
  RawScriptPubKey
}
import org.bitcoins.core.script.control.OP_IF
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object CreateConditionalScriptDialog {

  def showAndWait(parentWindow: Window): Option[ConditionalScriptPubKey] = {
    val dialog = new Dialog[Option[ConditionalScriptPubKey]]() {
      initOwner(parentWindow)
      title = "Create Conditional Script"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val trueCaseTF = new TextField() {
      promptText = "Hex Encoded Script"
    }

    val falseCaseTF = new TextField() {
      promptText = "Hex Encoded Script"
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("True Case Script"), 0, 0)
      add(trueCaseTF, 1, 0)

      add(new Label("False Case Script"), 0, 1)
      add(falseCaseTF, 1, 1)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== trueCaseTF.text.isEmpty || falseCaseTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val trueScriptStr = trueCaseTF.text.value
        val trueScript = RawScriptPubKey(trueScriptStr)

        val falseScriptStr = trueCaseTF.text.value
        val falseScript = RawScriptPubKey(falseScriptStr)

        Some(ConditionalScriptPubKey(OP_IF, trueScript, falseScript))
      } else None

    dialog.showAndWait() match {
      case Some(Some(spk: ConditionalScriptPubKey)) =>
        Some(spk)
      case Some(_) | None =>
        None
    }
  }
}
