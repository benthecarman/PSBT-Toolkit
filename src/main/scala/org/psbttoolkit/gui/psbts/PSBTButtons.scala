package org.psbttoolkit.gui.psbts

import scalafx.scene.control.Button

class PSBTButtons(model: PSBTsPaneModel) {

  private val addSignature: Button = new Button("Add Signature") {
    onAction = _ => model.addSignature()
  }

  private val finalizePSBT: Button = new Button("Finalize PSBT") {
    onAction = _ => model.finalizePSBT()
  }

  val all: Vector[Button] = Vector(finalizePSBT, addSignature)
}
