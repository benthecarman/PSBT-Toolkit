package org.psbttoolkit.gui.generators

import javafx.event.ActionEvent
import scalafx.scene.control.{Button, ComboBox}

class GeneratorButtons(model: GeneratorPaneModel) {

  val psbtGenSelector: ComboBox[String] = new ComboBox(PSBTGenType.names) {
    value = PSBTGenType.ArbitraryPSBT.toString

    onAction = (_: ActionEvent) => {
      GeneratorData.psbtGenType = PSBTGenType.fromString(value.value)
    }
  }

  val psbtGenButton: Button = new Button("Generate PSBT") {
    onAction = _ => model.genPSBT()
  }

  val txGenSelector: ComboBox[String] = new ComboBox(TransactionGenType.names) {
    value = TransactionGenType.ArbitraryTransaction.toString

    onAction = (_: ActionEvent) => {
      GeneratorData.txGenType = TransactionGenType.fromString(value.value)
    }
  }

  val txGenButton: Button = new Button("Generate Transaction") {
    onAction = _ => model.genTx()
  }
}
