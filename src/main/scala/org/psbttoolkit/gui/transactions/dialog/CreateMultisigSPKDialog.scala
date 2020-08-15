package org.psbttoolkit.gui.transactions.dialog

import org.bitcoins.core.protocol.script.MultiSignatureScriptPubKey
import org.bitcoins.crypto.ECPublicKey
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.stage.Window

object CreateMultisigSPKDialog {

  def showAndWait(parentWindow: Window): Option[MultiSignatureScriptPubKey] = {
    val dialog = new Dialog[Option[MultiSignatureScriptPubKey]]() {
      initOwner(parentWindow)
      title = "Create Multisig Script"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets
    dialog.resizable = true

    val requiredSignersTF = new TextField()

    val keyMap: scala.collection.mutable.Map[Int, TextField] =
      scala.collection.mutable.Map.empty

    var nextKeyRow: Int = 2
    val keyGrid: GridPane = new GridPane {
      alignment = Pos.Center
      padding = Insets(top = 10, right = 10, bottom = 10, left = 10)
      hgap = 5
      vgap = 5
    }

    def addKeyRow(): Unit = {

      val keyTF = new TextField() {
        promptText = "Hex encoded"
      }
      val row = nextKeyRow
      keyMap.addOne((row, keyTF))

      keyGrid.add(new Label("Pub Key"), 0, row)
      keyGrid.add(keyTF, 1, row)

      nextKeyRow += 1
      dialog.dialogPane().getScene.getWindow.sizeToScene()
    }

    addKeyRow()
    addKeyRow()

    val addKeyButton: Button = new Button("+") {
      onAction = _ => addKeyRow()
    }

    dialog.dialogPane().content = new VBox() {
      padding = Insets(20, 10, 10, 10)
      spacing = 10
      alignment = Pos.Center

      val globalData: Node = new HBox() {
        spacing = 10
        alignment = Pos.Center
        children = Vector(new Label("Required Signers"), requiredSignersTF)
      }

      val keys: Node = new VBox {
        alignment = Pos.Center

        val label: HBox = new HBox {
          alignment = Pos.Center
          spacing = 10
          children = Vector(new Label("Pub Keys"), addKeyButton)
        }
        children = Vector(label, keyGrid)
      }

      children = Vector(globalData, new Separator(), keys)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== requiredSignersTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {
        val requiredSigners = requiredSignersTF.text.value.toInt

        val keyStrs = keyMap.values
        val keys = keyStrs.flatMap { keyStr =>
          if (keyStr.text.value.nonEmpty) {
            val key = ECPublicKey(keyStr.text.value)
            Some(key)
          } else {
            None
          }
        }.toVector

        val spk = MultiSignatureScriptPubKey(requiredSigners, keys)

        Some(spk)
      } else None

    dialog.showAndWait() match {
      case Some(Some(spk: MultiSignatureScriptPubKey)) =>
        Some(spk)
      case Some(_) | None => None
    }
  }
}
