package org.psbttoolkit.gui.transactions.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes.jfxDialogPane2sfx
import scalafx.scene.control.{ButtonType, Dialog, Label}
import scalafx.stage.Window

object DecodeTransactionDialog {

  def showAndWait(parentWindow: Window, decoded: String): Unit = {

    val _ = new Dialog[Unit]() {
      initOwner(parentWindow)
      title = "Decoded Transaction"
      dialogPane().buttonTypes = Seq(ButtonType.OK)
      dialogPane().stylesheets = GlobalData.currentStyleSheets
      dialogPane().content = new Label(decoded) {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
      }
      dialogPane().getScene.getWindow.sizeToScene()
    }.showAndWait()
  }
}
