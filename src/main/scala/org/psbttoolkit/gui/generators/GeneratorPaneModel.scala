package org.psbttoolkit.gui.generators

import org.bitcoins.core.protocol.transaction.TxUtil
import org.bitcoins.testkitcore.Implicits._
import org.bitcoins.testkitcore.gen._
import org.psbttoolkit.gui.TaskRunner
import org.psbttoolkit.gui.generators.types.PSBTGenType._
import org.psbttoolkit.gui.generators.types.TransactionGenType._
import scalafx.beans.property.ObjectProperty
import scalafx.stage.Window

class GeneratorPaneModel() {
  var taskRunner: TaskRunner = _

  // Sadly, it is a Java "pattern" to pass null into
  // constructors to signal that you want some default
  val parentWindow: ObjectProperty[Window] =
    ObjectProperty[Window](null.asInstanceOf[Window])

  def genPSBT(): Unit = {

    lazy val data = GeneratorData.psbtGenType match {
      case ArbitraryPSBT =>
        PSBTGenerators.arbitraryPSBT.sampleSome.base64
      case FinalizedPSBT =>
        PSBTGenerators.finalizedPSBT.sampleSome.base64
      case FullNonFinalizedPSBT =>
        PSBTGenerators.fullNonFinalizedPSBT.sampleSome.base64
      case PSBTWithUnknowns =>
        PSBTGenerators.psbtWithUnknowns.sampleSome.base64
      case PSBTWithUnknownVersion =>
        PSBTGenerators.psbtWithUnknownVersion.sampleSome.base64
    }

    taskRunner.run(caption = s"Generate ${GeneratorData.psbtGenType}",
                   op = GeneratorData.generatedData.value = data)
  }

  def genTx(): Unit = {
    taskRunner.run(
      caption = s"Generate ${GeneratorData.txGenType}",
      op = {
        val data = GeneratorData.txGenType match {
          case ArbitraryTransaction =>
            TransactionGenerators.transaction.sampleSome.hex
          case BaseTransaction =>
            TransactionGenerators.baseTransaction.sampleSome.hex
          case WitnessTransaction =>
            TransactionGenerators.witnessTransaction.sampleSome.hex
          case UnsignedTransaction =>
            val signedTx = TransactionGenerators.transaction.sampleSome
            val unsignedTx = TxUtil.emptyAllScriptSigs(signedTx)
            unsignedTx.hex
        }
        GeneratorData.generatedData.value = data
      }
    )
  }
}
