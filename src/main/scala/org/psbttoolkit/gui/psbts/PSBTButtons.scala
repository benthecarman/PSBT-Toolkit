package org.psbttoolkit.gui.psbts

import scalafx.scene.control.Button

class PSBTButtons(model: PSBTsPaneModel) {

  val globalButtons: Vector[Button] = Vector()

  private val addSignature: Button = new Button("Add Signature") {
    onAction = _ => model.addSignature()
  }

  private val addNonWitnessUTXO: Button = new Button("Add Non-Witness UTXO") {
    onAction = _ => model.addNonWitnessUTXO()
  }

  private val addWitnessUTXO: Button = new Button("Add Witness UTXO") {
    onAction = _ => model.addNonWitnessUTXO()
  }

  private val addInputRedeemScript: Button = new Button(
    "Add Input Redeem Script") {
    onAction = _ => model.addInputRedeemScript()
  }

  private val addInputKeyPath: Button = new Button("Add Input Key Path") {
    onAction = _ => model.addInputKeyPath()
  }

  private val addSigHashType: Button = new Button("Add Sig Hash Type") {
    onAction = _ => model.addSigHashType()
  }

  val inputButtons: Seq[Button] =
    Vector(addNonWitnessUTXO,
           addWitnessUTXO,
           addSignature,
           addInputRedeemScript,
           addInputKeyPath,
           addSigHashType)

  private val addOutputRedeemScript: Button = new Button(
    "Add Output Redeem Script") {
    onAction = _ => model.addOutputRedeemScript()
  }

  private val addOutputKeyPath: Button = new Button("Add Output Key Path") {
    onAction = _ => model.addOutputKeyPath()
  }

  val outputButtons: Vector[Button] =
    Vector(addOutputRedeemScript, addOutputKeyPath)

  private val finalizePSBT: Button = new Button("Finalize PSBT") {
    onAction = _ => model.finalizePSBT()
  }

  val generalButtons: Vector[Button] = Vector(finalizePSBT)
}
