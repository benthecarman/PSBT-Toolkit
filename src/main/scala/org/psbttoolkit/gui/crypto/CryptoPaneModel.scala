package org.psbttoolkit.gui.crypto

import org.bitcoins.crypto.{ECPrivateKey, ECPublicKey, NetworkElement}
import org.psbttoolkit.gui.TaskRunner
import org.psbttoolkit.gui.crypto.dialog._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.TextArea
import scalafx.stage.Window

class CryptoPaneModel(resultArea: TextArea) {
  var taskRunner: TaskRunner = _

  // Sadly, it is a Java "pattern" to pass null into
  // constructors to signal that you want some default
  val parentWindow: ObjectProperty[Window] =
    ObjectProperty[Window](null.asInstanceOf[Window])

  def setResult(str: String): Unit = {
    resultArea.text = str
  }

  def setResult(tx: NetworkElement): Unit = {
    setResult(tx.hex)
  }

  def genPrivateKey(): Unit = {
    taskRunner.run("Generate Private Key",
                   setResult(ECPrivateKey.freshPrivateKey))
  }

  def genPublicKey(): Unit = {
    taskRunner.run("Generate Public Key", setResult(ECPublicKey.freshPublicKey))
  }

  def privKeyToPubKey(): Unit = {
    val resultOpt = ConvertToPublicKeyDialog.showAndWait(parentWindow.value)

    taskRunner.run(
      caption = "Convert to Pubkey",
      op = resultOpt match {
        case Some(key) =>
          setResult(key)
        case None =>
          ()
      }
    )
  }

  def hashData(): Unit = {
    val resultOpt = HashDataDialog.showAndWait(parentWindow.value)

    taskRunner.run(
      caption = "Hash Data",
      op = resultOpt match {
        case Some(hash) =>
          setResult(hash)
        case None =>
          ()
      }
    )
  }

  def signData(): Unit = {
    val resultOpt = SignDataDialog.showAndWait(parentWindow.value)

    taskRunner.run(
      caption = "Sign Data",
      op = resultOpt match {
        case Some(sig) =>
          setResult(sig)
        case None =>
          ()
      }
    )
  }

  def adaptSignature(): Unit = {
    val resultOpt = AdaptSignatureDialog.showAndWait(parentWindow.value)

    taskRunner.run(
      caption = "Adapt Signature",
      op = resultOpt match {
        case Some(sig) =>
          setResult(sig)
        case None =>
          ()
      }
    )
  }

  def flipEndianness(): Unit = {
    val resultOpt = FlipEndiannessDialog.showAndWait(parentWindow.value)

    taskRunner.run(
      caption = "Flip Endianness",
      op = resultOpt match {
        case Some(bytes) =>
          setResult(bytes.toHex)
        case None =>
          ()
      }
    )
  }
}
