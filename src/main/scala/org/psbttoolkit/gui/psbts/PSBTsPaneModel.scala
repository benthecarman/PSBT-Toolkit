package org.psbttoolkit.gui.psbts

import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.stream.Materializer
import akka.util.ByteString
import org.bitcoins.commons.jsonmodels.SerializedPSBT
import org.bitcoins.core.config.{MainNet, RegTest, SigNet, TestNet3}
import org.bitcoins.core.protocol.transaction.Transaction
import org.bitcoins.core.psbt.GlobalPSBTRecord.{Version, XPubKey}
import org.bitcoins.core.psbt._
import org.bitcoins.crypto.DoubleSha256DigestBE
import org.psbttoolkit.gui.GlobalData.{bitcoindRpc, system, useBitcoind}
import org.psbttoolkit.gui.psbts.dialog._
import org.psbttoolkit.gui.transactions.dialog.DecodedDataDialog
import org.psbttoolkit.gui.{GlobalData, TaskRunner}
import play.api.libs.json.Json
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.TextArea
import scalafx.stage.Window
import scodec.bits.ByteVector
import ujson._

import scala.collection.mutable
import scala.concurrent.{ExecutionContextExecutor, Future}
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

  def setResult(psbt: PSBT): Unit = {
    setResult(psbt.base64)
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

  def decodePSBT(): Unit = {
    val resultOpt = getPSBTOpt.map { psbt =>
      val decoded = SerializedPSBT.decodePSBT(psbt)
      DecodedDataDialog.showAndWait(parentWindow.value,
                                    "Decoded PSBT",
                                    Json.prettyPrint(decoded.toJson))
    }

    taskRunner.run(
      caption = "Decode PSBT",
      op = getPSBTOpt match {
        case Some(_) =>
          resultOpt.map(_ => ())
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def analyzePSBT(): Unit = {
    val resultOpt = getPSBTOpt.map { psbt =>
      val inputs = psbt.inputMaps.zipWithIndex.map {
        case (inputMap, index) =>
          val txIn = psbt.transaction.inputs(index)
          val vout = txIn.previousOutput.vout.toInt
          val nextRole = inputMap.nextRole(txIn)
          val hasUtxo = inputMap.prevOutOpt(vout).isDefined
          val isFinalized = inputMap.isFinalized
          val missingSigs = inputMap.missingSignatures(vout)

          if (missingSigs.isEmpty) {
            Obj(
              "has_utxo" -> Bool(hasUtxo),
              "is_final" -> Bool(isFinalized),
              "next" -> Str(nextRole.shortName)
            )
          } else {
            Obj(
              "has_utxo" -> Bool(hasUtxo),
              "is_final" -> Bool(isFinalized),
              "missing_sigs" -> missingSigs.map(hash => Str(hash.hex)),
              "next" -> Str(nextRole.shortName)
            )
          }
      }

      val optionalsJson: Vector[(String, Num)] = {
        val fee =
          psbt.feeOpt.map(fee => "fee" -> Num(fee.satoshis.toLong.toDouble))
        val vsize =
          psbt.estimateVSize.map(vsize =>
            "estimated_vsize" -> Num(vsize.toDouble))
        val feeRate = psbt.estimateSatsPerVByte.map(feeRate =>
          "estimated_sats_vbyte" -> Num(feeRate.toLong.toDouble))

        Vector(fee, vsize, feeRate).flatten
      }

      val inputJson = Vector("inputs" -> Arr.from(inputs))
      val nextRoleJson: Vector[(String, Str)] =
        Vector("next" -> Str(psbt.nextRole.shortName))

      val jsonVec: Vector[(String, Value)] =
        inputJson ++ optionalsJson ++ nextRoleJson
      val jsonMap = mutable.LinkedHashMap(jsonVec: _*)
      val json = Obj(jsonMap)

      DecodedDataDialog.showAndWait(parentWindow.value,
                                    "Analyzed PSBT",
                                    json.render(2))
    }

    taskRunner.run(
      caption = "Decode PSBT",
      op = getPSBTOpt match {
        case Some(_) =>
          resultOpt.map(_ => ())
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
            case Some((index, extKey, pubkey, path)) =>
              val updated = psbt.addKeyPathToInput(extKey, path, pubkey, index)
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
            case Some((index, extKey, pubkey, path)) =>
              val updated = psbt.addKeyPathToOutput(extKey, path, pubkey, index)
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

  def combinePSBTs(): Unit = {
    val resultOpt: Option[PSBT] = getPSBTOpt.flatMap { _ =>
      CombinePSBTs.showAndWait(parentWindow.value)
    }

    taskRunner.run(
      caption = "Combine PSBTs",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some(other) =>
              val updated = psbt.combinePSBT(other)
              setResult(updated.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }

  def addBitcoindData(): Unit = {
    implicit val m: Materializer = Materializer(system)
    implicit val ec: ExecutionContextExecutor = m.executionContext

    def makeCall(txIdBE: DoubleSha256DigestBE): Future[Transaction] = {
      if (useBitcoind) {
        bitcoindRpc.getRawTransactionRaw(txIdBE)
      } else {
        val url = GlobalData.network match {
          case MainNet =>
            s"https://blockstream.info/api/tx/${txIdBE.hex}/hex"
          case TestNet3 =>
            s"https://blockstream.info/testnet/api/tx/${txIdBE.hex}/hex"
          case RegTest | SigNet =>
            throw new IllegalArgumentException(
              "Unable broadcast a regtest or signet transaction")
        }

        Http()
          .singleRequest(Get(url))
          .flatMap(response =>
            response.entity.dataBytes
              .runFold(ByteString.empty)(_ ++ _)
              .map(payload =>
                Transaction.fromHex(payload.decodeString(ByteString.UTF_8))))
      }
    }

    taskRunner.run[Future[Unit]](
      caption = "Fetch Data",
      op = getPSBTOpt match {
        case Some(emptyPsbt) =>
          val prevTxIdBEs =
            emptyPsbt.transaction.inputs.map(_.previousOutput.txIdBE)

          val prevTxFs = prevTxIdBEs.map(makeCall)
          Future.sequence(prevTxFs).map { prevTxs =>
            val psbt = prevTxs.zipWithIndex.foldLeft(emptyPsbt) {
              case (accumPSBT, (tx, index)) =>
                accumPSBT.addUTXOToInput(tx, index)
            }

            setResult(psbt)
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }
}
