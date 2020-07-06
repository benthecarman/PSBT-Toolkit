package org.psbttoolkit.gui.transactions

import org.psbttoolkit.gui.GlobalData.system
import scalafx.scene.control.Button

class TransactionsButtons(model: TransactionsPaneModel) {

  private val broadcastTx: Button = new Button("Broadcast Transaction") {
    onAction = _ => model.broadcastTx()
  }

  private val decodeTx: Button = new Button("Decode Transaction") {
    onAction = _ => model.decodeTransaction()
  }

  val all: Vector[Button] = Vector(broadcastTx, decodeTx)
}
