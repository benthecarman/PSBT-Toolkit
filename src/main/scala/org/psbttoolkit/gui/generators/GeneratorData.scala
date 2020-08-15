package org.psbttoolkit.gui.generators

import org.psbttoolkit.gui.generators.types._
import scalafx.beans.property.StringProperty

object GeneratorData {
  var psbtGenType: PSBTGenType = PSBTGenType.ArbitraryPSBT

  var txGenType: TransactionGenType = TransactionGenType.ArbitraryTransaction

  val generatedData: StringProperty = StringProperty("")
}
