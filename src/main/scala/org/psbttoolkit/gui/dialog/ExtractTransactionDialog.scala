package org.psbttoolkit.gui.dialog

import org.bitcoins.core.protocol.transaction.Transaction
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.scene.control.{ButtonType, Dialog, Label, TextArea}
import scalafx.scene.layout.VBox
import scalafx.stage.Window

object ExtractTransactionDialog {

  def showAndWait(
      parentWindow: Window,
      transaction: Transaction,
      errOpt: Option[Throwable]): Unit = {
    val dialog = new Dialog[Unit]() {
      initOwner(parentWindow)
      title = "Extract Transaction"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets

    val labelOpt = errOpt.map(err =>
      new Label(
        s"Warning! This transaction does not appear to be valid, error ${err.getMessage}"))

    val rawTxTA = new TextArea {
      text = transaction.hex
      editable = false
    }

    dialog.dialogPane().content = new VBox {
      children = Vector(labelOpt, Some(rawTxTA)).flatten
    }

    val _ = dialog.showAndWait()
  }
}
