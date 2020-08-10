package org.psbttoolkit.gui

import org.bitcoins.core.psbt.PSBT
import org.psbttoolkit.gui.generators.GeneratorsPane
import org.psbttoolkit.gui.psbts.PSBTsPane
import org.psbttoolkit.gui.psbts.dialog.{AboutDialog, QRCodeDialog}
import org.psbttoolkit.gui.transactions.TransactionsPane
import scalafx.beans.property.ObjectProperty
import scalafx.scene.image.ImageView
import scalafx.stage.Window

class HomeGUIModel(
    psbtPane: PSBTsPane,
    txPane: TransactionsPane,
    genPane: GeneratorsPane) {

  // Sadly, it is a Java "pattern" to pass null into
  // constructors to signal that you want some default
  val parentWindow: ObjectProperty[Window] =
    ObjectProperty[Window](null.asInstanceOf[Window])

  def onAbout(): Unit = {
    AboutDialog.showAndWait(parentWindow.value)
  }

  def onQR(img: ImageView): Unit = {
    QRCodeDialog.showAndWait(parentWindow.value, img)
  }

  def setPSBTResult(str: String): Unit = {
    psbtPane.model.setResult(str)
  }

  def getPSBTOpt: Option[PSBT] = {
    psbtPane.model.getPSBTOpt
  }

  def setTransactionResult(str: String): Unit = {
    txPane.model.setResult(str)
  }
}
