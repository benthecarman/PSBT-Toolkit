package org.psbttoolkit.gui.transactions.dialog

import org.bitcoins.core.protocol.script.{
  P2SHScriptPubKey,
  P2WSHWitnessSPKV0,
  RawScriptPubKey,
  ScriptPubKey
}
import org.psbttoolkit.gui.GlobalData
import org.psbttoolkit.gui.transactions.types.P2SHScriptType
import org.psbttoolkit.gui.transactions.types.P2SHScriptType._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object CreateP2SHScriptDialog {

  def showAndWait(parentWindow: Window): Option[ScriptPubKey] = {
    val dialog = new Dialog[Option[ScriptPubKey]]() {
      initOwner(parentWindow)
      title = "Create P2SH Script"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val scriptTF = new TextField() {
      promptText = "Hex Encoded"
    }

    var scriptType: P2SHScriptType = P2SH

    val scriptTypeSelector: ComboBox[String] = new ComboBox(
      P2SHScriptType.names) {
      value = P2SH.toString

      onAction = (_: ActionEvent) => {
        scriptType = P2SHScriptType.fromString(value.value)
      }
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Redeem Script"), 0, 0)
      add(scriptTF, 1, 0)
      add(scriptTypeSelector, 2, 0)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== scriptTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        scriptType match {
          case P2SH =>
            val scriptStr = scriptTF.text.value
            val script = ScriptPubKey(scriptStr)
            Some(P2SHScriptPubKey(script))
          case P2WSH =>
            val scriptStr = scriptTF.text.value
            val script = RawScriptPubKey(scriptStr)
            Some(P2WSHWitnessSPKV0(script))
        }
      } else None

    dialog.showAndWait() match {
      case Some(Some(spk: ScriptPubKey)) =>
        Some(spk)
      case Some(_) | None =>
        None
    }
  }
}
