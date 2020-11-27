package org.psbttoolkit.gui.transactions.dialog

import javafx.scene.paint.Color
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes.jfxDialogPane2sfx
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control._
import scalafx.scene.text.Text
import scalafx.stage.Window

object DecodedDataDialog {

  def showAndWait(
      parentWindow: Window,
      titleStr: String,
      decoded: String): Unit = {

    val _ = new Dialog[Unit]() {
      initOwner(parentWindow)
      title = titleStr
      dialogPane().buttonTypes = Seq(ButtonType.OK)
      dialogPane().stylesheets = GlobalData.currentStyleSheets

      private val content = if (decoded.length > 2000) {
        new TextArea(decoded)
      } else {
        new ScrollPane() {
          content = new Text(decoded) {
            fill <== ObjectProperty(Color.WHITE)
          }
        }
      }

      dialogPane().content = content
      resizable = true
    }.showAndWait()
  }
}
