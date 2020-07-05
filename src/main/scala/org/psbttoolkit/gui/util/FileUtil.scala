package org.psbttoolkit.gui.util

import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

object FileUtil {
  val psbtExtensionFilter = new ExtensionFilter("psbt", "*.psbt")
  val allExtensionFilter = new ExtensionFilter("All Files", "*")

  def getPSBTFileChooser(titleStr: String): FileChooser = {
    new FileChooser() {
      title = titleStr
      extensionFilters.addAll(psbtExtensionFilter, allExtensionFilter)
      selectedExtensionFilter = psbtExtensionFilter
      initialFileName = "MyPSBT.psbt"
    }
  }
}
