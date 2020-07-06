package org.psbttoolkit.gui.psbts

import org.bitcoins.core.psbt.PSBT
import org.psbttoolkit.gui.TaskRunner
import org.psbttoolkit.gui.dialog._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.TextArea
import scalafx.stage.Window

import scala.util.{Failure, Success, Try}

class PSBTsPaneModel(resultArea: TextArea) {
  var taskRunner: TaskRunner = _

  // Sadly, it is a Java "pattern" to pass null into
  // constructors to signal that you want some default
  val parentWindow: ObjectProperty[Window] =
    ObjectProperty[Window](null.asInstanceOf[Window])

  def setResult(str: String): Unit = {
    resultArea.text = str
  }

  def getPSBTOpt: Option[PSBT] = {
    Try(PSBT.fromString(resultArea.text.value)).toOption
  }

  def finalizePSBT(): Unit = {
    taskRunner.run(
      caption = "Finalize PSBT",
      op = getPSBTOpt match {
        case Some(psbt) =>
          psbt.finalizePSBT match {
            case Success(finalized) =>
              setResult(finalized.base64)
            case Failure(exception) =>
              throw exception
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addSignature(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddSignatureDialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Signature",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, pubKey, sig)) =>
              val updated = psbt.addSignature(pubKey, sig, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addNonWitnessUTXO(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddNonWitnessUTXODialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Non-Witness UTXO",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, tx)) =>
              val updated = psbt.addUTXOToInput(tx, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addWitnessUTXO(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddWitnessUTXODialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Witness UTXO",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, output)) =>
              val updated = psbt.addWitnessUTXOToInput(output, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addInputRedeemScript(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddRedeemScriptDialog.showAndWait(isInput = true, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Input Redeem Script",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, spk)) =>
              val updated = psbt.addRedeemOrWitnessScriptToInput(spk, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addOutputRedeemScript(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddRedeemScriptDialog.showAndWait(isInput = false, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Output Redeem Script",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, spk)) =>
              val updated = psbt.addRedeemOrWitnessScriptToOutput(spk, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addInputKeyPath(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddKeyPathDialog.showAndWait(isInput = true, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Input Key Path",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, extKey, path)) =>
              val updated = psbt.addKeyPathToInput(extKey, path, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addOutputKeyPath(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddKeyPathDialog.showAndWait(isInput = false, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Output Key Path",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, extKey, path)) =>
              val updated = psbt.addKeyPathToOutput(extKey, path, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addSigHashType(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddSigHashTypeDialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Sig Hash Type",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, hashType)) =>
              val updated = psbt.addSigHashTypeToInput(hashType, index)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def extractTransaction(): Unit = {
    val resultOpt = getPSBTOpt.map { psbt =>
      val validityT = psbt.extractTransactionAndValidate
      val (tx, errorOpt) = validityT match {
        case Success(tx) => (tx, None)
        case Failure(err) =>
          (psbt.extractTransaction, Some(err))
      }
      ExtractTransactionDialog.showAndWait(parentWindow.value, tx, errorOpt)
    }

    taskRunner.run(
      caption = "Extract Transaction",
      op = getPSBTOpt match {
        case Some(_) =>
          resultOpt.map(_ => ())
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }
}
