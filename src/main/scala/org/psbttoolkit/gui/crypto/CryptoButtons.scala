package org.psbttoolkit.gui.crypto

import scalafx.scene.control.Button

class CryptoButtons(model: CryptoPaneModel) {

  private val genPrivateKey: Button = new Button("Generate Private Key") {
    onAction = _ => model.genPrivateKey()
  }

  private val genPublicKey: Button = new Button("Generate Public Key") {
    onAction = _ => model.genPublicKey()
  }

  private val privKeyToPubKey: Button = new Button("Private Key to Pubkey") {
    onAction = _ => model.privKeyToPubKey()
  }

  private val flipEndianness: Button = new Button("Flip Endianness") {
    onAction = _ => model.flipEndianness()
  }

  private val hashData: Button = new Button("Hash Data") {
    onAction = _ => model.hashData()
  }

  private val signData: Button = new Button("Sign Data") {
    onAction = _ => model.signData()
  }

  val all: Vector[Button] = Vector(genPrivateKey,
                                   genPublicKey,
                                   privKeyToPubKey,
                                   flipEndianness,
                                   hashData,
                                   signData)

  // Set them to all have the same width as the largest button
  private val largest = all.maxBy(_.getWidth)

  def setMinWidth(): Unit = {
    all.foreach(_.minWidth <== largest.width)
  }
}
