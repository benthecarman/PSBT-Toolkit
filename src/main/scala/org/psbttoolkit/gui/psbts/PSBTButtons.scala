package org.psbttoolkit.gui.psbts

import scalafx.scene.control.Button

class PSBTButtons(model: PSBTsPaneModel) {

  private val addXPub: Button = new Button("Add XPub") {
    onAction = _ => model.addGlobalXPub()
  }

  private val setVersion: Button = new Button("Set Version") {
    onAction = _ => model.setVersion()
  }

  private val addGlobalUnknown: Button = new Button("Add Unknown") {
    onAction = _ => model.addGlobalUnknown()
  }

  val globalButtons: Vector[Button] =
    Vector(addXPub, setVersion, addGlobalUnknown)

  private val finalizeInput: Button = new Button("Finalize Input") {
    onAction = _ => model.finalizeInput()
  }

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

  private val addInputUnknown: Button = new Button("Add Unknown") {
    onAction = _ => model.addInputUnknown()
  }

  val inputButtons: Seq[Button] =
    Vector(finalizeInput,
           addNonWitnessUTXO,
           addWitnessUTXO,
           addSignature,
           addInputRedeemScript,
           addInputKeyPath,
           addSigHashType,
           addInputUnknown)

  private val addOutputRedeemScript: Button = new Button(
    "Add Output Redeem Script") {
    onAction = _ => model.addOutputRedeemScript()
  }

  private val addOutputKeyPath: Button = new Button("Add Output Key Path") {
    onAction = _ => model.addOutputKeyPath()
  }

  private val addOutputUnknown: Button = new Button("Add Unknown") {
    onAction = _ => model.addOutputUnknown()
  }

  val outputButtons: Vector[Button] =
    Vector(addOutputRedeemScript, addOutputKeyPath, addOutputUnknown)

  private val fromUnsignedTx: Button = new Button("From Unsigned Transaction") {
    onAction = _ => model.fromUnsignedTransaction()
  }

  private val combinePSBTs: Button = new Button("Combine PSBTs") {
    onAction = _ => model.combinePSBTs()
  }

  private val finalizePSBT: Button = new Button("Finalize PSBT") {
    onAction = _ => model.finalizePSBT()
  }

  private val extractTx: Button = new Button("Extract Transaction") {
    onAction = _ => model.extractTransaction()
  }

  private val fetchData: Button = new Button("Fetch Data") {
    onAction = _ => model.addBlockExplorerData()
  }

  val generalButtons: Vector[Button] =
    Vector(fromUnsignedTx, combinePSBTs, finalizePSBT, extractTx, fetchData)

  private val all: Vector[Button] =
    globalButtons ++ inputButtons ++ outputButtons ++ generalButtons

  // Set them to all have the same width as the largest button
  // TODO: figure out how todo this dynamically
  private val largest = fromUnsignedTx

  def setMinWidth(): Unit = {
    all.foreach(_.minWidth <== largest.width)
  }
}
