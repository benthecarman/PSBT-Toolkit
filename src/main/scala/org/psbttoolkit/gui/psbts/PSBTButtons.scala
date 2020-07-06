package org.psbttoolkit.gui.psbts

import scalafx.scene.control.Button

class PSBTButtons(model: PSBTsPaneModel) {

  private val addSignature: Button = new Button("Add Signature") {
    onAction = _ => model.addSignature()
  }

  private val finalizePSBT: Button = new Button("Finalize PSBT") {
    onAction = _ => model.finalizePSBT()
  }

  val globalButtons: Vector[Button] = Vector()

  val inputButtons: Seq[Button] = Vector(addSignature, addSignature)

  val outputButtons: Vector[Button] = Vector()

  val generalButtons: Vector[Button] = Vector(finalizePSBT)
}
