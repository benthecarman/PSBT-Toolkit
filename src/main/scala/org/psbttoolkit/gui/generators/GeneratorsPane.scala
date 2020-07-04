package org.psbttoolkit.gui.generators

import scalafx.geometry.Orientation
import scalafx.scene.control.{Separator, TextArea}
import scalafx.scene.layout.{BorderPane, HBox}

class GeneratorsPane {

  private val resultArea = new TextArea {
    editable = false
    wrapText = true
    text <== GeneratorData.generatedData
    promptText = "Generated Data"
  }

  private val txSide = new HBox {
    children =
      List(GeneratorButtons.txGenSelector, GeneratorButtons.txGenButton)
    spacing = 10
  }

  private val psbtSide = new HBox {
    children =
      List(GeneratorButtons.psbtGenSelector, GeneratorButtons.psbtGenButton)
    spacing = 10
  }

  private val sep: Separator = new Separator() {
    orientation = Orientation.Vertical
  }

  private val buttonPane = new HBox {
    children = List(txSide, sep, psbtSide)
    spacing = 10
  }

  val view: BorderPane = new BorderPane {
    center = buttonPane
    bottom = resultArea
  }

  buttonPane.prefHeight <== (view.height * 2) / 3
  resultArea.prefHeight <== (view.height / 3)
}
