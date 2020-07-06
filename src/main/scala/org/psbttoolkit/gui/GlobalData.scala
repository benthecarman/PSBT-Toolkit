package org.psbttoolkit.gui

import akka.actor.ActorSystem
import org.bitcoins.core.config._
import org.psbttoolkit.gui.settings.Themes
import scalafx.beans.property.StringProperty

object GlobalData {

  implicit val system: ActorSystem = ActorSystem("psbt-toolkit")

  val log: StringProperty = StringProperty("")

  val statusText: StringProperty = StringProperty("")

  var darkThemeEnabled: Boolean = true

  def currentStyleSheets: Seq[String] =
    if (GlobalData.darkThemeEnabled) {
      Seq(Themes.DarkTheme.fileLocation)
    } else {
      Seq.empty
    }

  var network: BitcoinNetwork = TestNet3
}
