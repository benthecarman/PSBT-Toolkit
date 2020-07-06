package org.psbttoolkit.gui.dialog

import org.bitcoins.core.protocol.transaction.Transaction
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, Dialog, TextArea}
import scalafx.scene.layout.VBox
import scalafx.stage.Window

object ExtractTransactionDialog {

  def showAndWait(
      parentWindow: Window,
      transaction: Transaction,
      errOpt: Option[Throwable]): Unit = {

    val rawTxTA = new TextArea {
      text = transaction.hex
      wrapText = true
      editable = false
    }

    val _ = errOpt match {
      case Some(err) =>
        new Alert(AlertType.Warning) {
          initOwner(owner)
          title = "Extract Transaction"
          headerText =
            s"Warning! This transaction does not appear to be valid, error ${err.getMessage}"
          dialogPane().content = rawTxTA
          dialogPane().stylesheets = GlobalData.currentStyleSheets
        }.showAndWait()
      case None =>
        new Dialog[Unit]() {
          initOwner(parentWindow)
          title = "Extract Transaction"
          dialogPane().buttonTypes = Seq(ButtonType.OK)
          dialogPane().stylesheets = GlobalData.currentStyleSheets
          dialogPane().content = new VBox {
            children = rawTxTA
          }
        }.showAndWait()
    }
  }
}
