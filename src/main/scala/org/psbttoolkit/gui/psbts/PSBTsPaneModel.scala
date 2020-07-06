package org.psbttoolkit.gui.psbts

import org.bitcoins.core.psbt.GlobalPSBTRecord.{Version, XPubKey}
import org.bitcoins.core.psbt._
import org.psbttoolkit.gui.TaskRunner
import org.psbttoolkit.gui.dialog._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.TextArea
import scalafx.stage.Window
import scodec.bits.ByteVector

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

  def finalizeInput(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      FinalizeInputDialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Finalize Input",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some(index) =>
              psbt.finalizeInput(index) match {
                case Success(finalized) =>
                  setResult(finalized.base64)
                case Failure(exception) =>
                  throw exception
              }
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

  def fromUnsignedTransaction(): Unit = {
    val resultOpt = PSBTFromUnsignedTransaction.showAndWait(parentWindow.value)

    taskRunner.run(
      caption = "Create PSBT from unsigned transaction",
      op = resultOpt match {
        case Some(txT) =>
          txT match {
            case Success(tx) =>
              val psbt = PSBT.fromUnsignedTx(tx)
              setResult(psbt.base64)
            case Failure(_) =>
              throw new RuntimeException("Transaction not correctly serialized")
          }
        case None =>
          ()
      }
    )
  }

  def setVersion(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      SetVersionDialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Set PSBT Version",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some(version) =>
              val oldGlobal = psbt.globalMap.filterNot(rec =>
                PSBTGlobalKeyId(rec.key) == PSBTGlobalKeyId.VersionKeyId)
              val newGlobal =
                GlobalPSBTMap((oldGlobal :+ Version(version)).toVector)
              val updated = PSBT(newGlobal, psbt.inputMaps, psbt.outputMaps)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addGlobalXPub(): Unit = {
    val resultOpt = getPSBTOpt.flatMap { _ =>
      AddGlobalXPubKey.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Global XPub Key",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((xpub, fingerprint, path)) =>
              val newGlobal =
                GlobalPSBTMap(
                  (psbt.globalMap :+ XPubKey(xpub, fingerprint, path)).toVector)
              val updated = PSBT(newGlobal, psbt.inputMaps, psbt.outputMaps)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addGlobalUnknown(): Unit = {
    val resultOpt: Option[(ByteVector, ByteVector)] = getPSBTOpt.flatMap { _ =>
      AddGlobalUnknownDialog.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Global Unknown",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((key, data)) =>
              val newGlobal =
                GlobalPSBTMap(
                  (psbt.globalMap :+ GlobalPSBTRecord.Unknown(key,
                                                              data)).toVector)
              val updated = psbt.copy(globalMap = newGlobal)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addInputUnknown(): Unit = {
    val resultOpt: Option[(Int, ByteVector, ByteVector)] = getPSBTOpt.flatMap {
      _ =>
        AddUnknownDialog.showAndWait(isInput = true, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Input Unknown",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, key, data)) =>
              val newInput =
                InputPSBTMap(
                  (psbt.inputMaps(index) :+ InputPSBTRecord
                    .Unknown(key, data)).toVector)

              val newInputs = psbt.inputMaps.updated(index, newInput)
              val updated = psbt.copy(inputMaps = newInputs)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addOutputUnknown(): Unit = {
    val resultOpt: Option[(Int, ByteVector, ByteVector)] = getPSBTOpt.flatMap {
      _ =>
        AddUnknownDialog.showAndWait(isInput = false, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Output Unknown",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, key, data)) =>
              val newOutput =
                OutputPSBTMap(
                  (psbt.outputMaps(index) :+ OutputPSBTRecord
                    .Unknown(key, data)).toVector)

              val newOutputs = psbt.outputMaps.updated(index, newOutput)
              val updated = psbt.copy(outputMaps = newOutputs)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }
}
