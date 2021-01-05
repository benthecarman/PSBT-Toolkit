package org.psbttoolkit.gui.psbts.dialog

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

    val version: String = getClass.getPackage.getImplementationVersion

    dialog.dialogPane().content = new TextArea {
      text =
        s"PSBT Toolkit v$version\n\nCreated by: benthecarman\n\nRepo is: https://github.com/benthecarman/PSBT-Toolkit"
      editable = false
    }

    val _ = dialog.showAndWait()
  }
}
