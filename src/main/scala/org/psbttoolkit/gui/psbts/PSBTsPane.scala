package org.psbttoolkit.gui.psbts

import javafx.scene.input.KeyEvent
import org.bitcoins.core.psbt.PSBT
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{BorderPane, TilePane}

import scala.util.Try

class PSBTsPane {

  private val resultArea = new TextArea {
    editable = true
    wrapText = true
    promptText = "PSBT"
    text <== PSBTData.str
    onKeyTyped = (e: KeyEvent) => {
//      PSBTData.str.value = PSBTData.str.
      PSBTData.psbtOpt = Try(PSBT.fromString(text.value)).toOption
    }
  }

  private val buttonPane = new TilePane {
    children = List(PSBTButtons.finalizePSBT)
  }

  val view: BorderPane = new BorderPane {
    center = buttonPane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)
}
