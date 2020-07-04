package org.psbttoolkit.gui.psbts

import scalafx.scene.control.Button

import scala.util.{Failure, Success}

object PSBTButtons {

  val finalizePSBT: Button = new Button("Finalize PSBT") {
    onAction = _ => {
      PSBTData.psbtOpt match {
        case Some(psbt) =>
          psbt.finalizePSBT match {
            case Success(finalized) =>
              PSBTData.psbtOpt = Some(finalized)
            case Failure(exception) =>
              println(exception.getMessage)
            // todo dialog
          }
        case None =>
          println("No PSBT")
        // todo error dialog
      }
    }
  }
}
