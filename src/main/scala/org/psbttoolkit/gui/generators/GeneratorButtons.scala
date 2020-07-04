package org.psbttoolkit.gui.generators

import javafx.event.ActionEvent
import org.bitcoins.testkit.Implicits._
import org.bitcoins.testkit.core.gen._
import org.psbttoolkit.gui.generators.PSBTGenType._
import org.psbttoolkit.gui.generators.TransactionGenType._
import scalafx.scene.control.{Button, ComboBox}

import scala.concurrent.ExecutionContext.Implicits.global

object GeneratorButtons {

  val psbtGenSelector: ComboBox[String] = new ComboBox(PSBTGenType.names) {
    value = PSBTGenType.ArbitraryPSBT.toString

    onAction = (_: ActionEvent) => {
      GeneratorData.psbtGenType = PSBTGenType.fromString(value.value)
    }
  }

  val psbtGenButton: Button = new Button("Generate PSBT") {
    onAction = _ => {

      val dataF = GeneratorData.psbtGenType match {
        case ArbitraryPSBT =>
          PSBTGenerators.arbitraryPSBT.sampleSome.map(_.hex)
        case FinalizedPSBT =>
          PSBTGenerators.finalizedPSBT.sampleSome.map(_.hex)
        case FullNonFinalizedPSBT =>
          PSBTGenerators.fullNonFinalizedPSBT.sampleSome.map(_.hex)
        case PSBTWithUnknowns =>
          PSBTGenerators.psbtWithUnknowns.sampleSome.map(_.hex)
        case PSBTWithUnknownVersion =>
          PSBTGenerators.psbtWithUnknownVersion.sampleSome.map(_.hex)
      }

      dataF.map(data => GeneratorData.generatedData.value = data)
    }
  }

  val txGenSelector: ComboBox[String] = new ComboBox(TransactionGenType.names) {
    value = TransactionGenType.ArbitraryTransaction.toString

    onAction = (_: ActionEvent) => {
      GeneratorData.txGenType = TransactionGenType.fromString(value.value)
    }
  }

  val txGenButton: Button = new Button("Generate Transaction") {
    onAction = _ => {

      val data = GeneratorData.txGenType match {
        case ArbitraryTransaction =>
          TransactionGenerators.transaction.sampleSome.hex
        case BaseTransaction =>
          TransactionGenerators.baseTransaction.sampleSome.hex
        case WitnessTransaction =>
          TransactionGenerators.witnessTransaction.sampleSome.hex
      }

      GeneratorData.generatedData.value = data
    }
  }
}
