package org.psbttoolkit.gui.transactions

import scalafx.scene.control.Button

class TransactionsButtons(model: TransactionsPaneModel) {

  private val broadcastTx: Button = new Button("Broadcast Transaction") {
    onAction = _ => model.broadcastTx()
  }

  val all: Vector[Button] = Vector(broadcastTx)
}
