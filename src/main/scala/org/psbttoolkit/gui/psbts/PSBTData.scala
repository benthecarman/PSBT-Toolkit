package org.psbttoolkit.gui.psbts

import org.bitcoins.core.psbt.PSBT
import scalafx.beans.property.StringProperty

object PSBTData {
  var psbtOpt: Option[PSBT] = None

  val str: StringProperty = StringProperty("")
}
