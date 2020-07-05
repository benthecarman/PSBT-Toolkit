package org.psbttoolkit.gui.psbts

import org.bitcoins.core.psbt.PSBT
import org.psbttoolkit.gui.TaskRunner
import org.psbttoolkit.gui.dialog.AddSignatureDialog
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
    val resultOpt = getPSBTOpt.flatMap { psbt =>
      AddSignatureDialog.showAndWait(psbt.inputMaps.size, parentWindow.value)
    }

    taskRunner.run(
      caption = "Add Signature",
      op = getPSBTOpt match {
        case Some(psbt) =>
          resultOpt match {
            case Some((index, pubKey, sig)) =>
              val addedSig = psbt.addSignature(pubKey, sig, index)
              setResult(addedSig.base64)
            case None =>
              ()
          }
        case None =>
          throw new RuntimeException("Missing PSBT")
      }
    )
  }
}
