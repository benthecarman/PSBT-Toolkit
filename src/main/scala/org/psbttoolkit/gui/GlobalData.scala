package org.psbttoolkit.gui

import akka.actor.ActorSystem
import org.bitcoins.core.config._
import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindInstance
import org.psbttoolkit.gui.settings.Themes
import scalafx.beans.property.StringProperty

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

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

  var network: BitcoinNetwork = MainNet

  lazy val bitcoindInstance: BitcoindInstance =
    BitcoindInstance.fromConfigFile()
  lazy val bitcoindRpc: BitcoindRpcClient = BitcoindRpcClient(bitcoindInstance)

  lazy val useBitcoind: Boolean = {
    val startedF = bitcoindRpc.isStartedF

    Await.result(startedF, 30.seconds)
  }
}
