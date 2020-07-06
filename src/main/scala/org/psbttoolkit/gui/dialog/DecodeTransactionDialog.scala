package org.psbttoolkit.gui.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.scene.control.{ButtonType, Dialog, TextArea}
import scalafx.stage.Window

object DecodeTransactionDialog {

  def showAndWait(parentWindow: Window, decoded: String): Unit = {

    val _ = new Dialog[Unit]() {
      initOwner(parentWindow)
      title = "Decoded Transaction"
      dialogPane().buttonTypes = Seq(ButtonType.OK)
      dialogPane().stylesheets = GlobalData.currentStyleSheets
      // todo dynamically size the text area
      dialogPane().content = new TextArea {
        text = decoded
        editable = false
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
      }
    }.showAndWait()
  }
}
