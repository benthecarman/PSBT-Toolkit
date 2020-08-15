package org.psbttoolkit.gui.transactions.dialog

import org.bitcoins.core.protocol.script.{
  P2PKHScriptPubKey,
  P2PKScriptPubKey,
  P2WPKHWitnessSPKV0,
  ScriptPubKey
}
import org.bitcoins.crypto.ECPublicKey
import org.psbttoolkit.gui.GlobalData
import org.psbttoolkit.gui.transactions.PubKeyScriptType
import org.psbttoolkit.gui.transactions.PubKeyScriptType._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object CreatePubKeyScriptDialog {

  def showAndWait(parentWindow: Window): Option[ScriptPubKey] = {
    val dialog = new Dialog[Option[ScriptPubKey]]() {
      initOwner(parentWindow)
      title = "Create Public Key Script"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val pubkeyTF = new TextField() {
      promptText = "Hex Encoded"
    }

    var scriptType: PubKeyScriptType = P2WPKH

    val scriptTypeSelector: ComboBox[String] = new ComboBox(
      PubKeyScriptType.names) {
      value = P2WPKH.toString

      onAction = (_: ActionEvent) => {
        scriptType = PubKeyScriptType.fromString(value.value)
      }
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Public Key"), 0, 0)
      add(pubkeyTF, 1, 0)
      add(scriptTypeSelector, 2, 0)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== pubkeyTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val keyStr = pubkeyTF.text.value
        val key = ECPublicKey(keyStr)

        scriptType match {
          case P2WPKH =>
            Some(P2WPKHWitnessSPKV0(key))
          case P2PKH =>
            Some(P2PKHScriptPubKey(key))
          case P2PK =>
            Some(P2PKScriptPubKey(key))
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
