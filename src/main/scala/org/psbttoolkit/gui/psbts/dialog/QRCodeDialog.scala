package org.psbttoolkit.gui.psbts.dialog

import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.scene.control.{ButtonType, Dialog}
import scalafx.scene.image.ImageView
import scalafx.stage.Window

object QRCodeDialog {

  def showAndWait(parentWindow: Window, img: ImageView): Unit = {
    val dialog = new Dialog[Unit]() {
      initOwner(parentWindow)
      title = "QR Code"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    dialog.dialogPane().content = img
    val _ = dialog.showAndWait()
  }
}
