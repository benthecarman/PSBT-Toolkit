package org.psbttoolkit.gui

import org.psbttoolkit.gui.dialog.GetNewAddressDialog
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.stage.Window

class HomeGUIModel() {
  var taskRunner: TaskRunner = _

  // Sadly, it is a Java "pattern" to pass null into
  // constructors to signal that you want some default
  val parentWindow: ObjectProperty[Window] =
    ObjectProperty[Window](null.asInstanceOf[Window])

  def onGetNewAddress(): Unit = {
    val address = StringProperty("")

    GetNewAddressDialog.showAndWait(parentWindow.value, address)
  }

}
