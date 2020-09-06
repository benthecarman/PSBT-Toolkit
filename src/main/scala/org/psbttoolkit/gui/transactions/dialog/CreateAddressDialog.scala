package org.psbttoolkit.gui.transactions.dialog

import org.bitcoins.core.protocol.script.{
  P2PKHScriptPubKey,
  P2SHScriptPubKey,
  WitnessScriptPubKey
}
import org.bitcoins.core.protocol.{
  Bech32Address,
  BitcoinAddress,
  P2PKHAddress,
  P2SHAddress
}
import org.psbttoolkit.gui.GlobalData
import org.psbttoolkit.gui.transactions.types.AddressType
import org.psbttoolkit.gui.transactions.types.AddressType._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.stage.Window

object CreateAddressDialog {

  def showAndWait(parentWindow: Window): Option[BitcoinAddress] = {
    val dialog = new Dialog[Option[BitcoinAddress]]() {
      initOwner(parentWindow)
      title = "Create Address"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val scriptTF = new TextField() {
      promptText = "Hex Encoded"
    }

    var addressType: AddressType = Bech32AddressType

    val addressTypeSelector: ComboBox[String] = new ComboBox(
      AddressType.names) {
      value = Bech32AddressType.shortName

      onAction = (_: ActionEvent) => {
        addressType = AddressType.fromString(value.value)
      }
    }

    dialog.dialogPane().content = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("Script"), 0, 0)
      add(scriptTF, 1, 0)
      add(addressTypeSelector, 2, 0)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== scriptTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val scriptStr = scriptTF.text.value

        addressType match {
          case AddressType.LegacyAddressType =>
            val spk = P2PKHScriptPubKey(scriptStr)
            Some(P2PKHAddress(spk, GlobalData.network))
          case AddressType.P2SHAddressType =>
            val p2sh = P2SHScriptPubKey(scriptStr)
            Some(P2SHAddress(p2sh, GlobalData.network))
          case AddressType.Bech32AddressType =>
            val witSpk = WitnessScriptPubKey(scriptStr)
            Some(Bech32Address(witSpk, GlobalData.network))
        }
      } else None

    dialog.showAndWait() match {
      case Some(Some(addr: BitcoinAddress)) =>
        Some(addr)
      case Some(_) | None =>
        None
    }
  }
}
