package org.psbttoolkit.gui

import org.bitcoins.core.config._
import org.psbttoolkit.gui.settings.Themes
import scalafx.beans.property.StringProperty

object GlobalData {
  val log: StringProperty = StringProperty("")

  val statusText: StringProperty = StringProperty("")

  val darkThemeEnabled: Boolean = true

  def currentStyleSheets: Seq[String] =
    if (GlobalData.darkThemeEnabled) {
      Seq(Themes.DarkTheme.fileLocation)
    } else {
      Seq.empty
    }

  var network: BitcoinNetwork = TestNet3
}
