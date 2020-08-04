package org.psbttoolkit.gui.crypto

import scalafx.scene.control.Button

class CryptoButtons(model: CryptoPaneModel) {

  val genPrivateKey: Button = new Button("Generate Private Key") {
    onAction = _ => model.genPrivateKey()
  }

  val genPublicKey: Button = new Button("Generate Public Key") {
    onAction = _ => model.genPublicKey()
  }

  val hashData: Button = new Button("Hash Data") {
    onAction = _ => model.hashData()
  }

  val all: Vector[Button] = Vector(genPrivateKey, genPublicKey, hashData)

  // Set them to all have the same width as the largest button
  private val largest = all.maxBy(_.getWidth)

  def setMinWidth(): Unit = {
    all.foreach(_.minWidth <== largest.width)
  }
}
