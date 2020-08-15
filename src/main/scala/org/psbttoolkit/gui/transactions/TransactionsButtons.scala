package org.psbttoolkit.gui.transactions

import scalafx.scene.control.Button

class TransactionsButtons(model: TransactionsPaneModel) {

  private val broadcastTx: Button = new Button("Broadcast Transaction") {
    onAction = _ => model.broadcastTx()
  }

  private val decodeTx: Button = new Button("Decode Transaction") {
    onAction = _ => model.decodeTransaction()
  }

  private val constructTransaction: Button = new Button(
    "Construct Transaction") {
    onAction = _ => model.constructTransaction()
  }

  val txButtons: Vector[Button] =
    Vector(broadcastTx, decodeTx, constructTransaction)

  private val createPubKeyScript: Button = new Button("Create Pub Key Script") {
    onAction = _ => model.createPubKeyScript()
  }

  private val createP2SHScript: Button = new Button("Create P2SH Script") {
    onAction = _ => model.createP2SHScript()
  }

  val spkButtons: Vector[Button] = Vector(createPubKeyScript, createP2SHScript)

  val all: Vector[Button] = txButtons ++ spkButtons

  private val largest = all.maxBy(_.getWidth)

  def setMinWidth(): Unit = {
    all.foreach(_.minWidth <== largest.width)
  }
}
