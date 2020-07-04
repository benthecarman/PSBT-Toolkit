package org.psbttoolkit.gui.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.scene.control.{ButtonType, Dialog, TextArea}
import scalafx.stage.Window

object AboutDialog {

  def showAndWait(parentWindow: Window): Unit = {
    val dialog = new Dialog[Unit]() {
      initOwner(parentWindow)
      title = "About"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    dialog.dialogPane().content = new TextArea {
      text = "I haven't written this yet, but remember to stack sats"
      editable = false
    }

    val _ = dialog.showAndWait()
  }
}
